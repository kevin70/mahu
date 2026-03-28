package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.entity.sys.StoredObject;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class StoredObjectRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<StoredObject> OBJECT_MODEL = Instancio.of(StoredObject.class)
            .ignore(field(StoredObject::getCreatedAt))
            .ignore(field(StoredObject::getUpdatedAt))
            .ignore(field(StoredObject::getMetadata))
            .toModel();

    private StoredObjectRepository repo() {
        return new StoredObjectRepository(db());
    }

    @Test
    void updateStatus_updates_matching_rows_only() {
        // 验证批量状态更新返回命中数量，并确保落库状态一致。
        var a = storedObject("o1", StoredObject.Type.ADMIN_AVATAR, 0);
        var b = storedObject("o2", StoredObject.Type.ADMIN_AVATAR, 0);
        db().saveAll(List.of(a, b));

        var updated = repo().updateStatus(List.of("o1", "o2"), StoredObject.Type.ADMIN_AVATAR, 7);

        assertThat(updated).isEqualTo(2);
        assertThat(db().find(StoredObject.class, "o1").getStatus()).isEqualTo(7);
        assertThat(db().find(StoredObject.class, "o2").getStatus()).isEqualTo(7);
    }

    private static StoredObject storedObject(String id, StoredObject.Type type, int status) {
        var o = Instancio.of(OBJECT_MODEL).create();
        o.setId(id);
        o.setUid(1L);
        o.setType(type);
        o.setStatus(status);
        o.setObjectKey(type.buildObjectKey(id, "png"));
        return o;
    }
}
