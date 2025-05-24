package cool.houge.mahu.admin.system.service;

import static cool.houge.mahu.admin.Utils.GEN_IGNORE_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.admin.TestBase;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import jakarta.inject.Inject;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.junit.jupiter.api.Test;

/// @author ZY (kzou227@qq.com)
class DictServiceTest extends TestBase {

    private static final InstancioApi<DictType> DICT_TYPE_GEN =
            Instancio.of(DictType.class).ignore(GEN_IGNORE_FIELDS);
    private static final InstancioApi<DictData> DICT_DATA_GEN =
            Instancio.of(DictData.class).ignore(GEN_IGNORE_FIELDS);

    @Inject
    DictService dictService;

    @Test
    void save() {
        var data1 = DICT_DATA_GEN.create();
        var data2 = DICT_DATA_GEN.create();

        var type = DICT_TYPE_GEN.create();
        type.setData(List.of(data1, data2));

        dictService.save(type);
        assertThat(type.getCreatedAt()).isNotNull();
    }
}
