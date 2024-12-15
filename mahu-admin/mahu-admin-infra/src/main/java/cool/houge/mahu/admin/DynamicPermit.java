package cool.houge.mahu.admin;

import io.helidon.common.parameters.Parameters;

/// 动态权限校验
///
/// @param kind 类型
/// @param parameters  商店 ID
///
/// @author ZY (kzou227@qq.com)
public record DynamicPermit(String kind, Parameters parameters) {

    /// 商店类型
    public static final String KIND_SHOP = "SHOP";
}
