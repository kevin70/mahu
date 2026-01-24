package cool.houge.mahu.util;

import cool.houge.mahu.domain.Page;
import io.ebean.BeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.ebean.Query;
import io.ebean.cool.houge.NoTotalPagedList;
import io.ebean.typequery.IQueryBean;
import org.jspecify.annotations.NonNull;

/// 扩展数据管理基类
///
/// @author ZY (kzou227@qq.com)
public class HBeanRepository<I, T> extends BeanRepository<@NonNull I, @NonNull T> {

    protected HBeanRepository(Class<T> type, Database database) {
        super(type, database);
    }

    /// 分页查询
    ///
    /// @param query 查询对象
    /// @param page 分页参数
    /// @return this
    protected PagedList<T> findPage(Query<@NonNull T> query, Page page) {
        query.setFirstRow((int) page.getOffset()).setMaxRows(page.getPageSize());
        return page.isIncludeTotal() ? query.findPagedList() : new NoTotalPagedList<>(query.findPagedList());
    }

    /// 分页查询
    ///
    /// @param query 查询对象
    /// @param page 分页参数
    /// @return this
    protected PagedList<T> findPage(IQueryBean<T, ?> query, Page page) {
        query.setFirstRow((int) page.getOffset()).setMaxRows(page.getPageSize());
        return page.isIncludeTotal() ? query.findPagedList() : new NoTotalPagedList<>(query.findPagedList());
    }
}
