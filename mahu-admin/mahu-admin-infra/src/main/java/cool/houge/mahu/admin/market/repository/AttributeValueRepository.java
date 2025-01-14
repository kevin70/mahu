package cool.houge.mahu.admin.market.repository;

import cool.houge.mahu.entity.market.AttributeValue;
import cool.houge.mahu.entity.market.query.QAttributeValue;
import io.ebean.BeanRepository;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 商品属性值
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AttributeValueRepository extends BeanRepository<Integer, AttributeValue> {

    public AttributeValueRepository(Database db) {
        super(AttributeValue.class, db);
    }

    public AttributeValue findByAttributeIdAndValue(Integer attributeId, String value) {
        return new QAttributeValue(db())
                .attribute
                .id
                .eq(attributeId)
                .value
                .eq(value)
                .setIncludeSoftDeletes()
                .findOne();
    }
}
