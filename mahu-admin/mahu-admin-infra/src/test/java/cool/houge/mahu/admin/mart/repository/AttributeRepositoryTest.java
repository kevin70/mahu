package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.admin.TestBase;
import cool.houge.mahu.entity.mart.Attribute;
import cool.houge.mahu.entity.mart.AttributeValue;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
class AttributeRepositoryTest extends TestBase {

    @Inject
    AttributeRepository attributeRepository;

    @Test
    void save() {
        var entity = Instancio.of(Attribute.class)
                .ignore(fields().annotated(Id.class))
                .create();
        var values = Instancio.of(AttributeValue.class).ignore(fields().annotated(Id.class)).stream()
                .limit(5)
                .toList();
        entity.setAttributeValues(values);
        attributeRepository.save(entity);

        assertThat(entity.getId()).isNotNull();
    }
}
