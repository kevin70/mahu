package cool.houge.mahu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class DictRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<DictGroup> GROUP_MODEL = Instancio.of(DictGroup.class)
            .ignore(field(DictGroup::getCreatedAt))
            .ignore(field(DictGroup::getUpdatedAt))
            .ignore(field(DictGroup::getData))
            .toModel();

    private static final Model<Dict> DICT_MODEL = Instancio.of(Dict.class)
            .ignore(field(Dict::getCreatedAt))
            .ignore(field(Dict::getUpdatedAt))
            .ignore(field(Dict::getGroup))
            .toModel();

    private DictRepository repo() {
        return new DictRepository(db());
    }

    @Test
    void save_and_findById() {
        // 场景：保存字典后按主键查询；期望返回同一条记录并保留分组关联。
        var g = group("g1");
        var d = dict(-101, g, "l1", "v1");

        db().save(g);
        repo().save(d);

        var loaded = repo().findById(-101);

        assertThat(loaded).isNotNull();
        assertThat(loaded.getDc()).isEqualTo(-101);
        assertThat(loaded.getLabel()).isEqualTo("l1");
        assertThat(loaded.getGroup().getId()).isEqualTo("g1");
    }

    private static DictGroup group(String id) {
        var g = Instancio.of(GROUP_MODEL).create();
        g.setId(id);
        return g;
    }

    private static Dict dict(int dc, DictGroup group, String label, String value) {
        var d = Instancio.of(DICT_MODEL).create();
        d.setDc(dc);
        d.setGroup(group);
        d.setLabel(label);
        d.setValue(value);
        d.setEnabled(true);
        d.setOrdering(10);
        return d;
    }
}
