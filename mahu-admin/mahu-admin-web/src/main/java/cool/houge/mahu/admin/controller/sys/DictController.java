package cool.houge.mahu.admin.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.controller.HDictService;
import cool.houge.mahu.admin.oas.vo.SysDictTypeUpsertRequest;
import cool.houge.mahu.admin.sys.service.DictService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class DictController implements HDictService, WebSupport {

    final VoBeanMapper beanMapper;
    final DictService dictService;

    @Override
    public void addSysDictData(ServerRequest request, ServerResponse response) {
        //
    }

    @Override
    public void createSysDictType(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysDictTypeUpsertRequest.class);
        validate(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void deleteSysDictType(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");
        dictService.deleteByTypeCode(typeCode);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysDictType(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var typeCode = pathParams.get("type_code");

        var bean = dictService.findById(typeCode);
        var rs = beanMapper.toDictResponse(bean);
        response.send(rs);
    }

    @Override
    public void pageSysDictType(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = dictService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toDictResponse);
        response.send(rs);
    }

    @Override
    public void updateSysDictType(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var vo = request.content().as(SysDictTypeUpsertRequest.class).setTypeCode(pathParams.get(
            "type_code"));
        validate(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.update(bean);
        response.status(NO_CONTENT_204).send();
    }
}
