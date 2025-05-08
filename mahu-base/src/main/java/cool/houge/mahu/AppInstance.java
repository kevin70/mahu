package cool.houge.mahu;

import lombok.AllArgsConstructor;
import lombok.Getter;

/// 当前运行的应用实例
///
/// @author ZY (kzou227@qq.com)
@Getter
@AllArgsConstructor
public final class AppInstance {

    /// 应用名称
    private final String name;
    /// 应用版本
    private final String version;
    /// 应用节点ID（集群唯一）
    private final int nodeId;

    /// 返回完整的名称
    public String getQualifiedName() {
        return name + "-" + nodeId;
    }
}
