package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

import cool.houge.mahu.config.TerminalType;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.query.AuthClientQuery;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class AuthClientRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<AuthClient> CLIENT_MODEL = Instancio.of(AuthClient.class)
            .ignore(field(AuthClient::getCreatedAt))
            .ignore(field(AuthClient::getUpdatedAt))
            .ignore(field(AuthClient::getDeleted))
            .toModel();

    private AuthClientRepository repo() {
        return new AuthClientRepository(db());
    }

    @Test
    void obtainClient_returns_entity_or_throws() {
        var c1 = authClient("c1", TerminalType.BROWSER, "a1");
        c1.setClientSecret("s1");
        db().save(c1);

        assertThat(repo().obtainClient("c1").getClientId()).isEqualTo("c1");
        assertThatThrownBy(() -> repo().obtainClient("missing")).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findPage_filters_by_fields() {
        var c1 = authClient("c1", TerminalType.BROWSER, "a1");
        var c2 = authClient("c2", TerminalType.BROWSER, "a2");
        var c3 = authClient("c3", TerminalType.WECHAT_XCX, "wx1");
        db().saveAll(List.of(c1, c2, c3));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var byClientId =
                repo().findPage(AuthClientQuery.builder().clientId("c2").build(), page);
        assertThat(byClientId.getList()).extracting(AuthClient::getClientId).containsExactly("c2");

        var byTerminal = repo().findPage(
                        AuthClientQuery.builder()
                                .terminalType(TerminalType.BROWSER)
                                .build(),
                        page);
        assertThat(byTerminal.getList()).extracting(AuthClient::getClientId).contains("c1", "c2");

        var byWechatAppid =
                repo().findPage(AuthClientQuery.builder().wechatAppid("wx1").build(), page);
        assertThat(byWechatAppid.getList()).extracting(AuthClient::getClientId).containsExactly("c3");
    }

    private static AuthClient authClient(String clientId, TerminalType terminalType, String wechatAppid) {
        var c = Instancio.of(CLIENT_MODEL).create();
        c.setClientId(clientId);
        c.setClientSecret("sec_" + clientId);
        c.setTerminalType(terminalType);
        c.setWechatAppid(wechatAppid);
        c.setDeleted(false);
        return c;
    }
}
