package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.model.query.FeatureFlagQuery;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class FeatureFlagRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<FeatureFlag> FLAG_MODEL = Instancio.of(FeatureFlag.class)
            .ignore(field(FeatureFlag::getId))
            .ignore(field(FeatureFlag::getCreatedAt))
            .ignore(field(FeatureFlag::getUpdatedAt))
            .toModel();

    private FeatureFlagRepository repo() {
        return new FeatureFlagRepository(db());
    }

    @Test
    void findPage_filters_and_sorts_by_ordering_desc() {
        // 覆盖 enabled/name/code 三类过滤，并验证 ordering 倒序输出。
        var wechatPay = flag(1, "pay.wechat", "微信支付", true, 1);
        var alipay = flag(2, "pay.alipay", "支付宝", true, 3);
        var beta = flag(3, "exp.beta", "Beta", false, 2);
        db().saveAll(List.of(wechatPay, alipay, beta));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var enabled = repo().findPage(FeatureFlagQuery.builder().enabled(true).build(), page);
        assertThat(enabled.getList()).extracting(FeatureFlag::getCode).containsExactly("pay.alipay", "pay.wechat");

        var byName = repo().findPage(FeatureFlagQuery.builder().name("支付").build(), page);
        assertThat(byName.getList()).extracting(FeatureFlag::getCode).contains("pay.wechat", "pay.alipay");

        var byCode = repo().findPage(FeatureFlagQuery.builder().code("exp").build(), page);
        assertThat(byCode.getList()).extracting(FeatureFlag::getCode).containsExactly("exp.beta");
    }

    private static FeatureFlag flag(Integer id, String code, String name, boolean enabled, int ordering) {
        var f = Instancio.of(FLAG_MODEL).create();
        f.setId(id);
        f.setCode(code);
        f.setName(name);
        f.setEnabled(enabled);
        f.setOrdering(ordering);
        f.setDescription("d");
        f.setPreset(false);
        return f;
    }
}
