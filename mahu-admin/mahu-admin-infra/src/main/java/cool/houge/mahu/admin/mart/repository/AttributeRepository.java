package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.mart.Attribute;
import cool.houge.mahu.entity.mart.query.QAttribute;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

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
        var rsqlCtx = RSQLContext.of(qb).property(qb.name).property(qb.ordering);
        apply(dataFilter, rsqlCtx);
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
