package cool.houge.mahu.admin;

import cool.houge.mahu.AppInstance;
import cool.houge.mahu.Version;
import cool.houge.mahu.config.ConfigKeys;
import cool.houge.mahu.config.InfoConfig;
import cool.houge.mahu.util.DatabaseNodeIdProvider;
import io.helidon.config.Config;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 应用实例
///
/// @author ZY (kzou227@qq.com)
@Singleton
class AppInstanceProvider implements Supplier<AppInstance> {

    final AppInstance v;

    AppInstanceProvider(Config config, DataSource ds) {
        var infoConfig = InfoConfig.create(config.get(ConfigKeys.INFO));
        var nodeIdProvider = new DatabaseNodeIdProvider("select nextval('admin_node_seq')", ds);
        // 此处实现代表集群应用实例最多 256 个节点，如果节点超出 256，应该修改此处的逻辑
        var maxNode = 256;
        var nodeId = nodeIdProvider.getNodeId() % maxNode + 1;
        System.setProperty("tsid.node", String.valueOf(nodeId));
        System.setProperty("tsid.node.count", String.valueOf(maxNode));
        this.v = new AppInstance(infoConfig.name(), Version.VERSION, nodeId);
    }

    @Override
    public AppInstance get() {
        return v;
    }
}
