package cool.houge.mahu.admin;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.GeneratedValue;
import org.instancio.Select;
import org.instancio.SelectorGroup;

import static org.instancio.Select.fields;

/// 测试工具类
///
/// @author ZY (kzou227@qq.com)
public final class Utils {

    /// 生成数据忽略这些字段
    public static final SelectorGroup GEN_IGNORE_FIELDS = Select.all(
            fields().annotated(GeneratedValue.class),
            fields().annotated(WhenCreated.class),
            fields().annotated(WhenModified.class));
}
