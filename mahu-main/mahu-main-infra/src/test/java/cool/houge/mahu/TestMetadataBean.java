package cool.houge.mahu;

import cool.houge.mahu.common.Metadata;
import org.instancio.Instancio;

import java.util.UUID;

/// 测试元数据对象
///
/// @author ZY (kzou227@qq.com)
public class TestMetadataBean implements Metadata {

    @Override
    public String clientAddr() {
        return Instancio.gen().net().ip4().get();
    }

    @Override
    public String traceId() {
        var uuid = UUID.randomUUID();
        return UlidCreator.getUlid().toLowerCase();
    }
}
