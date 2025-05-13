package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.mart.Attribute;
import cool.houge.mahu.entity.mart.query.QAttribute;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;
import java.util.List;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AttributeRepository extends HBeanRepository<Integer, Attribute> {

    public AttributeRepository(Database db) {
        super(Attribute.class, db);
    }

    public PagedList<Attribute> findPage(DataFilter dataFilter) {
        var qb = new QAttribute(db());
        var filterFields = List.of(
                FilterField.builder().with(qb.name).build(),
                FilterField.builder().with(qb.ordering).build()
                //
                );

        super.apply(dataFilter, filterFields, qb);
        return qb.attributeValues
                .fetch()
                .attributeValues
                .ordering
                .desc()
                .attributeValues
                .value
                .asc()
                .findPagedList();
    }
}
