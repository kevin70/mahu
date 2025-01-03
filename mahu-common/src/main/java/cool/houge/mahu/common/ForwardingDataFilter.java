package cool.houge.mahu.common;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/// 将其所有方法调用转发到另一个数据过滤
///
/// 子类应该覆盖一个或多个方法，以便根据装饰器模式的需要修改支持数据过滤的行为。
///
/// @author ZY (kzou227@qq.com)
public abstract class ForwardingDataFilter implements DataFilter {

    protected ForwardingDataFilter() {
    }

    /// 返回方法转发到的支持委托实例
    protected abstract DataFilter delegate();

    @Override
    public boolean isIncludeDeleted() {
        return delegate().isIncludeDeleted();
    }

    @Override
    public boolean isNoTotalCount() {
        return delegate().isNoTotalCount();
    }

    @Override
    public @NonNull List<Sort> sorts() {
        return delegate().sorts();
    }
}
