package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.admin.TestBase;
import cool.houge.mahu.entity.mart.Asset;
import cool.houge.mahu.entity.mart.Shop;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

///
/// @author ZY (kzou227@qq.com)
class AssetRepositoryTest extends TestBase {

    @Inject
    AssetRepository assetRepository;

    @Test
    void save() {
        var entities = Instancio.of(Asset.class)
                .ignore(fields().annotated(Id.class))
                .ignore(fields().annotated(WhenCreated.class))
                .ignore(fields().annotated(WhenModified.class))
                .supply(field(Asset::getShop), () -> new Shop().setId(2))
                .supply(field(Asset::getUri), () -> "https://placehold.co/600x400")
                .stream()
                .limit(199)
                .toList();
        var n = assetRepository.saveAll(entities);
        assertThat(n).isEqualTo(entities.size());
    }
}
