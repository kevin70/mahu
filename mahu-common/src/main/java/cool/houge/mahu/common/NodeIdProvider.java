package cool.houge.mahu.common;

/// 节点 ID 提供者
///
/// TSID (Time-Sorted Unique ID) 是一种时间排序的唯一ID生成方案，其中包含节点ID(node id)用于分布式环境中的区分
///
/// @author ZY (kzou227@qq.com)
public interface NodeIdProvider {

    int getNodeId();
}
