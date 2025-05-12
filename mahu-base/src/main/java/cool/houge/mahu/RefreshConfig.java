package cool.houge.mahu;

import java.lang.annotation.*;

/// 可刷新配置
///
/// @author ZY (kzou227@qq.com)
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.CLASS)
public @interface RefreshConfig {}
