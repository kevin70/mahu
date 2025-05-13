package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permits.DEPARTMENT;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertDepartmentRequest;
import cool.houge.mahu.admin.system.service.DepartmentService;
import cool.houge.mahu.entity.system.Department;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 部门
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DepartmentController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final DepartmentService departmentService;

    @Inject
    public DepartmentController(VoBeanMapper beanMapper, DepartmentService departmentService) {
        this.beanMapper = beanMapper;
        this.departmentService = departmentService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/departments", authz(DEPARTMENT.R()).wrap(this::listDepartments));
        rules.get("/system/departments/{id:\\d+}", authz(DEPARTMENT.R()).wrap(this::getDepartment));
        rules.post("/system/departments", authz(DEPARTMENT.W()).wrap(this::createDepartment));
        rules.put("/system/departments/{id:\\d+}", authz(DEPARTMENT.W()).wrap(this::updateDepartment));
        rules.delete("/system/departments/{id:\\d+}", authz(DEPARTMENT.W()).wrap(this::deleteDepartment));
    }

    void listDepartments(ServerRequest request, ServerResponse response) {
        var filter = dataFilter(request);
        var plist = departmentService.findPage(filter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toDepartmentResponse);
        response.send(rs);
    }

    void createDepartment(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertDepartmentRequest.class);
        validate(vo);

        var entity = beanMapper.toDepartment(vo);
        departmentService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    void updateDepartment(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertDepartmentRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var entity = beanMapper.toDepartment(vo).setId(id);
        departmentService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    void deleteDepartment(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        departmentService.delete(new Department().setId(id));
        response.status(NO_CONTENT_204).send();
    }

    void getDepartment(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = departmentService.findById(id);
        response.send(beanMapper.toDepartmentResponse(bean));
    }
}
