package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.TestTransactionBase;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import jakarta.inject.Inject;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static cool.houge.mahu.admin.Utils.GEN_IGNORE_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;

/// @author ZY (kzou227@qq.com)
class DictServiceTest extends TestTransactionBase {

    private static final Model<DictType> DICT_TYPE_MODEL =
            Instancio.of(DictType.class).ignore(GEN_IGNORE_FIELDS).toModel();
    private static final Model<DictData> DICT_DATA_MODEL =
            Instancio.of(DictData.class).ignore(GEN_IGNORE_FIELDS).toModel();

    @Inject
    DictService dictService;

    @Test
    void save() {
        var data1 = Instancio.of(DICT_DATA_MODEL).create();
        var data2 = Instancio.of(DICT_DATA_MODEL).create();

        var type = Instancio.of(DICT_TYPE_MODEL).create();
        type.setData(List.of(data1, data2));

        dictService.save(type);
        assertThat(type.getCreatedAt()).isNotNull();
    }
}
