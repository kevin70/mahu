package cool.houge.mahu;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.mahu.common.Metadata;
import org.instancio.Instancio;

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
        return UlidCreator.getUlid().toLowerCase();
    }
}
