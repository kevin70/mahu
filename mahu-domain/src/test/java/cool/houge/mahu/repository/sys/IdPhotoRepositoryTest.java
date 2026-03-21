package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.entity.sys.IdPhoto;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class IdPhotoRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<IdPhoto> PHOTO_MODEL = Instancio.of(IdPhoto.class)
            .ignore(field(IdPhoto::getCreatedAt))
            .ignore(field(IdPhoto::getUpdatedAt))
            .ignore(field(IdPhoto::getMetadata))
            .toModel();

    private IdPhotoRepository repo() {
        return new IdPhotoRepository(db());
    }

    @Test
    void updateStatus_updates_matching_rows_only() {
        var a = idPhoto("p1", IdPhoto.Type.DEFAULT, 0);
        var b = idPhoto("p2", IdPhoto.Type.DEFAULT, 0);
        var c = idPhoto("p3", IdPhoto.Type.DEFAULT, 1);
        db().saveAll(List.of(a, b, c));

        var updated = repo().updateStatus(List.of("p1", "p2", "p3"), IdPhoto.Type.DEFAULT, 9);

        assertThat(updated).isEqualTo(3);
        assertThat(db().find(IdPhoto.class, "p1").getStatus()).isEqualTo(9);
        assertThat(db().find(IdPhoto.class, "p2").getStatus()).isEqualTo(9);
        assertThat(db().find(IdPhoto.class, "p3").getStatus()).isEqualTo(9);
    }

    private static IdPhoto idPhoto(String id, IdPhoto.Type type, int status) {
        var p = Instancio.of(PHOTO_MODEL).create();
        p.setId(id);
        p.setUid(1L);
        p.setType(type);
        p.setStatus(status);
        p.setObjectKey(type.buildObjectKey(id, "png"));
        return p;
    }
}
