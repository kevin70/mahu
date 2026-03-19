package cool.houge.mahu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.query.DictQuery;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import io.ebean.PagedList;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class DictGroupRepositoryTest extends PostgresLiquibaseTestBase {

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

    private DictGroupRepository repo() {
        return new DictGroupRepository(db());
    }

    @Test
    void findByIds_null_returns_all() {
        db().saveAll(List.of(group("g1"), group("g2"), group("g3")));

        var all = repo().findByIds(null);

        assertThat(all).extracting(DictGroup::getId).containsExactlyInAnyOrder("g1", "g2", "g3");
    }

    @Test
    void findByIds_filters_by_ids() {
        db().saveAll(List.of(group("g1"), group("g2"), group("g3")));

        var some = repo().findByIds(Set.of("g1", "g3"));

        assertThat(some).extracting(DictGroup::getId).containsExactlyInAnyOrder("g1", "g3");
    }

    @Test
    void findDictData_returns_one_or_null() {
        var g1 = groupWithDicts("g1", List.of(dict(101), dict(102)));
        db().save(g1);

        assertThat(repo().findDictData(101)).extracting(Dict::getDc).isEqualTo(101);
        assertThat(repo().findDictData(999999)).isNull();
    }

    @Test
    void findAllData_fetches_data_collection() {
        var g1 = groupWithDicts("g1", List.of(dict(101), dict(102)));
        var g2 = groupWithDicts("g2", List.of(dict(201)));
        db().saveAll(List.of(g1, g2));

        var groups = repo().findAllData();

        assertThat(groups).extracting(DictGroup::getId).contains("g1", "g2");
        var g1Loaded = groups.stream().filter(g -> g.getId().equals("g1")).findFirst().orElseThrow();
        assertThat(g1Loaded.getData()).extracting(Dict::getDc).containsExactlyInAnyOrder(101, 102);
    }

    @Test
    void findPage_filters_by_groupId_and_sorts_by_updatedAt_desc() {
        db().saveAll(List.of(group("g1"), group("g2"), group("g3")));

        // make ordering deterministic
        setUpdatedAt("g1", Instant.parse("2020-01-01T00:00:00Z"));
        setUpdatedAt("g2", Instant.parse("2020-01-02T00:00:00Z"));
        setUpdatedAt("g3", Instant.parse("2020-01-03T00:00:00Z"));

        var page = Page.builder().page(1).pageSize(10).includeTotal(true).build();
        var query = DictQuery.builder().build();

        PagedList<DictGroup> result = repo().findPage(query, page);

        assertThat(result.getList()).extracting(DictGroup::getId).containsExactly("g3", "g2", "g1");

        var onlyG2 = repo().findPage(DictQuery.builder().groupId("g2").build(), page);
        assertThat(onlyG2.getList()).extracting(DictGroup::getId).containsExactly("g2");
    }

    @Test
    void findPage_filters_by_dc_via_data() {
        db().save(groupWithDicts("g1", List.of(dict(101))));
        db().save(groupWithDicts("g2", List.of(dict(201), dict(202))));

        var page = Page.builder().page(1).pageSize(10).includeTotal(true).build();
        var result = repo().findPage(DictQuery.builder().dc(202).build(), page);

        assertThat(result.getList()).extracting(DictGroup::getId).containsExactly("g2");
    }

    private static DictGroup group(String id) {
        return Instancio.of(GROUP_MODEL)
                .set(field(DictGroup::getId), id)
                .create();
    }

    private static Dict dict(int dc) {
        return Instancio.of(DICT_MODEL)
                .set(field(Dict::getDc), dc)
                .create();
    }

    private static DictGroup groupWithDicts(String id, List<Dict> dicts) {
        var g = group(id);
        g.setData(dicts);
        dicts.forEach(d -> d.setGroup(g));
        return g;
    }

    private void setUpdatedAt(String groupId, Instant updatedAt) {
        db().update(DictGroup.class)
                .set("updatedAt", updatedAt)
                .where()
                .idEq(groupId)
                .update();
    }
}

