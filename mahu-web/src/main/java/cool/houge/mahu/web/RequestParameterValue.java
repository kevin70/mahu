package cool.houge.mahu.web;

import static com.google.common.base.Strings.lenientFormat;
import static java.util.Objects.requireNonNull;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.GenericType;
import io.helidon.common.mapper.MapperException;
import io.helidon.common.mapper.OptionalValue;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/// 字符串值
///
/// @author ZY (kzou227@qq.com)
public class RequestParameterValue implements OptionalValue<String> {

    private final OptionalValue<String> delegate;

    public RequestParameterValue(OptionalValue<String> delegate) {
        requireNonNull(delegate);
        this.delegate = new WrapOptionalValue<>(delegate);
    }

    @Override
    public <N> OptionalValue<N> as(Class<N> type) {
        return new WrapOptionalValue<>(delegate.as(type));
    }

    @Override
    public <N> OptionalValue<N> as(GenericType<N> type) {
        return new WrapOptionalValue<>(delegate.as(type));
    }

    @Override
    public <N> OptionalValue<N> as(Function<? super String, ? extends N> mapper) {
        return new WrapOptionalValue<>(delegate.as(mapper));
    }

    @Override
    public Optional<String> asOptional() throws MapperException {
        return delegate.asOptional();
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public String get() {
        return delegate.get();
    }

    private record WrapOptionalValue<T>(OptionalValue<T> delegate) implements OptionalValue<T> {

        @Override
        public <N> OptionalValue<N> as(Class<N> type) {
            return asWrap(() -> delegate.as(type));
        }

        @Override
        public <N> OptionalValue<N> as(GenericType<N> type) {
            return asWrap(() -> delegate.as(type));
        }

        @Override
        public <N> OptionalValue<N> as(Function<? super T, ? extends N> mapper) {
            return asWrap(() -> delegate.as(mapper));
        }

        @Override
        public Optional<T> asOptional() throws MapperException {
            return delegate.asOptional();
        }

        @Override
        public String name() {
            return delegate.name();
        }

        @Override
        public T get() {
            try {
                return delegate.get();
            } catch (NoSuchElementException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, lenientFormat("缺少参数[%s]", delegate.name()), e);
            }
        }

        private <N> OptionalValue<N> asWrap(Supplier<OptionalValue<N>> c) {
            try {
                return c.get();
            } catch (MapperException e) {
                throw new BizCodeException(
                        BizCodes.INVALID_ARGUMENT,
                        lenientFormat("参数[%s]的值 %s 类型映射错误", delegate.get(), delegate.name()),
                        e);
            }
        }
    }
}
