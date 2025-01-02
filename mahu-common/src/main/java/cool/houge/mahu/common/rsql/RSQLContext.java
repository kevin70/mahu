package cool.houge.mahu.common.rsql;

import io.ebean.typequery.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/// RSQL 查询上下文
///
/// @author ZY (kzou227@qq.com)
public class RSQLContext {

    private final Map<String, Property> properties = new HashMap<>();

    /// 查询对象
    private final QueryBean<?, ?> query;

    private RSQLContext(QueryBean<?, ?> query) {
        this.query = query;
    }

    public static RSQLContext of(QueryBean<?,?> query) {
        requireNonNull(query);
        return new RSQLContext(query);
    }

    public QueryBean<?, ?> queryBean() {
        return query;
    }

    public Property getProperty(String name) {
        return properties.get(name);
    }

    public RSQLContext property(PInteger<?> p) {
        return property(p.toString(), p);
    }

    public RSQLContext property(String alias, PInteger<?> p) {
        return property(alias, p, Integer::parseInt);
    }

    public RSQLContext property(PLong<?> p) {
        return property(p.toString(), p);
    }

    public RSQLContext property(String alias, PLong<?> p) {
        return property(alias, p, Long::parseLong);
    }

    public RSQLContext property(PString<?> p) {
        return property(p.toString(), p);
    }

    public RSQLContext property(String alias, PString<?> p) {
        return property(alias, p, Function.identity());
    }

    public RSQLContext property(PInstant<?> p) {
        return property(p.toString(), p);
    }

    public RSQLContext property(String alias, PInstant<?> p) {
        return property(alias, p, Instant::parse);
    }

    public RSQLContext property(PLocalDate<?> p) {
        return property(p.toString(), p);
    }

    public RSQLContext property(String alias, PLocalDate<?> p) {
        return property(alias, p, LocalDate::parse);
    }

    public RSQLContext property(String alias, PLocalDateTime<?> p) {
        return property(alias, p, LocalDateTime::parse);
    }

    ///
    public RSQLContext property(TQPropertyBase<?, ?> p, Function<String, ?> converter) {
        return property(p.toString(), p, converter);
    }

    public RSQLContext property(String alias, TQPropertyBase<?, ?> p, Function<String, ?> converter) {
        properties.put(alias, new Property(p, converter));
        return this;
    }

    public record Property(TQPropertyBase<?, ?> original, Function<String, ?> converter) {
        public String propertyName() {
            return original.toString();
        }
    }
}
