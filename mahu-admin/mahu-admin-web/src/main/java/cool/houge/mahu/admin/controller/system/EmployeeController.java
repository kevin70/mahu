package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertEmployeeRequest;
import cool.houge.mahu.admin.system.service.EmployeeService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.EMPLOYEE;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 职员
///
/// @author ZY (kzou227@qq.com
@Singleton
public class EmployeeController implements HttpService, WebSupport {

    @Inject
    EmployeeService employeeService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/employees", authz(EMPLOYEE.R()).wrap(this::listEmployees));
        rules.post("/system/employees", authz(EMPLOYEE.W()).wrap(this::addEmployee));
        rules.put("/system/employees/{id:\\d+}", authz(EMPLOYEE.W()).wrap(this::updateEmployee));
        rules.delete("/system/employees/{id:\\d+}", authz(EMPLOYEE.W()).wrap(this::deleteEmployee));
        rules.get("/system/employees/{id:\\d+}", authz(EMPLOYEE.R()).wrap(this::getEmployee));
    }

    private void listEmployees(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = employeeService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetEmployeeResponse);
        response.send(rs);
    }

    private void addEmployee(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertEmployeeRequest.class);
        validate(vo);

        var entity = beanMapper.toEmployee(vo);
        employeeService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void updateEmployee(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertEmployeeRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asLong().get();

        var entity = beanMapper.toEmployee(vo).setId(id);
        employeeService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteEmployee(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asLong().get();

        employeeService.delete(id);
        response.status(NO_CONTENT_204).send();
    }

    private void getEmployee(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asLong().get();

        var entity = employeeService.obtainById(id);
        var rs = beanMapper.toGetEmployeeResponse(entity);
        response.send(rs);
    }
}
