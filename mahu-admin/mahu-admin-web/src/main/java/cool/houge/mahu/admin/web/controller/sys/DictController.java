package cool.houge.mahu.admin.web.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.mapping.SysBeanMapper;
import cool.houge.mahu.admin.oas.controller.HDictService;
import cool.houge.mahu.admin.oas.vo.SysDictGroupUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictUpsertRequest;
import cool.houge.mahu.admin.sys.service.DictService;
import cool.houge.mahu.model.query.DictGroupQuery;
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

    private final SysBeanMapper beanMapper;
    private final DictService dictService;

    @Override
    public void createSysDictData(ServerRequest request, ServerResponse response) {
        var dictGroupId = dictGroupId(request);
        var vo = request.content().as(SysDictUpsertRequest.class);
        validate(vo);

        var bean = beanMapper.toDict(vo);
        dictService.save(dictGroupId, bean);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void createSysDictGroup(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysDictGroupUpsertRequest.class);
        validate(vo);

        var bean = beanMapper.toDictGroup(vo);
        dictService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void deleteSysDictGroup(ServerRequest request, ServerResponse response) {
        var dictGroupId = dictGroupId(request);
        dictService.deleteById(dictGroupId);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysDictGroup(ServerRequest request, ServerResponse response) {
        var dictGroupId = dictGroupId(request);

        var bean = dictService.findById(dictGroupId);
        var rs = beanMapper.toSysDictGroupResponse(bean);
        response.send(rs);
    }

    @Override
    public void pageSysDictGroup(ServerRequest request, ServerResponse response) {
        var qb = DictGroupQuery.builder();
        queryArg(request, "group_id").ifPresent(qb::groupId);
        queryInt(request, "dc").ifPresent(qb::dc);

        var page = page(request);
        var plist = dictService.findPage(qb.build(), page);
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysDictGroupResponse);
        response.send(rs);
    }

    @Override
    public void updateSysDictGroup(ServerRequest request, ServerResponse response) {
        var dictGroupId = dictGroupId(request);
        var vo = request.content().as(SysDictGroupUpsertRequest.class).setId(dictGroupId);
        validate(vo);

        var bean = beanMapper.toDictGroup(vo);
        dictService.update(bean);
        response.status(NO_CONTENT_204).send();
    }

    String dictGroupId(ServerRequest request) {
        return pathString(request, "group_id");
    }
}
