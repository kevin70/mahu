package cool.houge.mahu.shared.repository;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.mahu.entity.AssetKind;
import cool.houge.mahu.entity.AssetTransaction;
import cool.houge.mahu.entity.UserAsset;
import io.ebean.BeanRepository;
import io.ebean.Database;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;
import io.helidon.service.registry.Service;
import java.util.UUID;

/// 用户资产
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class UserAssetRepository extends BeanRepository<UUID, UserAsset> {

    // language=SQL
    private static final RawSql INCREMENT_BALANCE_SQL = RawSqlBuilder.parse("WITH ua AS (UPDATE user_asset"
                    + " SET balance=balance+:amount,total_in=total_in+:amount,updated_at=CURRENT_TIMESTAMP"
                    + " WHERE uid=:uid AND kind=:kind"
                    + " RETURNING balance AS balance_after,frozen AS frozen_after,total_in),"
                    + " tx AS ("
                    + "INSERT INTO asset_transaction (id,created_at,uid,kind,change_amount,balance_before,"
                    + "balance_after,frozen_before,frozen_after,direction,reference_id,idempotency_key,feature_id)"
                    + " SELECT :tx_id,CURRENT_TIMESTAMP(0),:uid,:kind,:amount,ua.balance_after-:amount,ua.balance_after,"
                    + "ua.frozen_after,ua.frozen_after,'credit',:reference_id,:idempotency_key,:feature_id"
                    + " FROM ua RETURNING *) SELECT * FROM tx")
            .create();
    // language=SQL
    private static final RawSql INCREMENT_UPSERT_BALANCE_SQL =
            RawSqlBuilder.parse("""
        with ua as (
            INSERT INTO user_asset (created_at, updated_at, uid, kind, balance, total_in)
                VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :uid, :kind, :amount, :amount)
                ON CONFLICT (uid, kind) DO UPDATE
                    SET balance = user_asset.balance + :amount,
                        total_in = user_asset.total_in + :amount,
                        updated_at = CURRENT_TIMESTAMP
                RETURNING balance as balance_after, frozen as frozen_after, total_in),
             tx as (
                 INSERT INTO asset_transaction (id, created_at, uid, kind, change_amount,
                                                balance_before, balance_after, frozen_before, frozen_after,
                                                direction, reference_id, idempotency_key, feature_id)
                     SELECT gen_random_uuid(),
                            CURRENT_TIMESTAMP,
                            :uid,
                            :kind,
                            :amount,
                            ua.balance_after - :amount,
                            ua.balance_after,
                            ua.frozen_after,
                            ua.frozen_after,
                            'credit',
                            :reference_id,
                            :idempotency_key,
                            :feature_id
                     FROM ua
                     returning *)
        select * from tx;
        """).create();

    public UserAssetRepository(Database db) {
        super(UserAsset.class, db);
    }

    public AssetTransaction incrementBalance(
            long uid, AssetKind kind, long amount, String referenceId, int featureId, String idempotencyKey) {
        var txId = UlidCreator.getMonotonicUlid().toString();
        var a = db().createQuery(AssetTransaction.class)
                .setRawSql(INCREMENT_BALANCE_SQL)
                .setParameter("uid", uid)
                .setParameter("kind", kind)
                .setParameter("amount", amount)
                .setParameter("idempotency_key", idempotencyKey)
                .setParameter("feature_id", featureId)
                .setParameter("tx_id", txId)
                .findOneOrEmpty();
        if (a.isPresent()) {
            return a.get();
        }

        var b = db().createQuery(AssetTransaction.class)
                .setParameter("uid", uid)
                .setParameter("kind", kind)
                .setParameter("amount", amount)
                .setParameter("idempotency_key", idempotencyKey)
                .setParameter("feature_id", featureId)
                .setParameter("tx_id", txId)
                .findOneOrEmpty();
        if (b.isEmpty()) {
            throw new IllegalStateException("资产增加失败");
        }
        return b.get();
    }

    public AssetTransaction decrementBalance(
            long uid, AssetKind kind, long amount, String referenceId, int featureId, String idempotencyKey) {
        //
        return null;
    }

    public void unfreeze() {
        //
    }
}
