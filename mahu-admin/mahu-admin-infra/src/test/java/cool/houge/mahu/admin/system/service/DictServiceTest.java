package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.TestTransactionBase;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import java.util.List;

/// @author ZY (kzou227@qq.com)
class DictServiceTest extends TestTransactionBase {

    private static final Model<DictType> DICT_TYPE_MODEL = Instancio.of(DictType.class)
        .ignore(Select.fields().annotated(WhenCreated.class))
        .ignore(Select.fields().annotated(WhenModified.class))
        .toModel();
    private static final Model<DictData> DICT_DATA_MODEL = Instancio.of(DictData.class)
        .ignore(Select.fields().annotated(Id.class))
        .ignore(Select.fields().annotated(WhenCreated.class))
        .ignore(Select.fields().annotated(WhenModified.class))
        .toModel();

    @Inject
    DictService dictService;

    @Test
    void save() {
        var data1 = Instancio.of(DICT_DATA_MODEL).create();
        var data2 = Instancio.of(DICT_DATA_MODEL).create();

        var type = Instancio.of(DICT_TYPE_MODEL).create();
        type.setData(List.of(data1, data2));

        dictService.save(type);

        disableRollback();
    }

}
