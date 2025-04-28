package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.TestTransactionBase;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.entity.system.Department;
import cool.houge.mahu.entity.system.Employee;
import cool.houge.mahu.entity.system.Role;
import io.ebean.bean.EntityBean;
import io.ebeaninternal.server.core.DefaultServer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// @author ZY (kzou227@qq.com)
class AuditPersistControllerTest extends TestTransactionBase {

    @Test
    void changeData() {
        var employeeId = 1;

        var server = (DefaultServer) db();
        var descriptor = server.descriptor(Employee.class);
        var dbEntity = server.find(Employee.class, employeeId);
        dbEntity.setNickname("abc")
                .setAvatar("TEST")
                .setDepartment(new Department().setId(1).setName("WORLD"))
                .setRoles(List.of(
                        new Role().setId(1).setName("HELLO"),
                        new Role().setId(2).setName("WORLD")));

        var auditPersistController = new AuditPersistController();
        var auditLog = new AdminAuditLog();
        auditPersistController.changedData(server, descriptor, (EntityBean) dbEntity, auditLog);

        assertThat(auditLog.getData())
                .isEqualTo(
                        "{\"nickname\":\"abc\",\"avatar\":\"TEST\",\"department\":{\"id\":1},\"roles\":[{\"id\":1,\"name\":\"HELLO\"},{\"id\":2,\"name\":\"WORLD\"}]}");
        assertThat(auditLog.getOldData()).isNotEmpty();
    }
}
