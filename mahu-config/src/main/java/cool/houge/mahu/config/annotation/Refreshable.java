package cool.houge.mahu.config.annotation;

import java.lang.annotation.*;

/// 可刷新的配置
///
/// @author ZY (kzou227@qq.com)
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.CLASS)
public @interface Refreshable {}
