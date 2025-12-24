package cool.houge.mahu.admin.controller.sys;

import static cool.houge.mahu.web.ServerRequestUtils.pathString;
import static io.helidon.http.Status.NO_CONTENT_204;

import com.google.common.base.Strings;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.oas.controller.HDictService;
import cool.houge.mahu.admin.oas.vo.SysDictTypeUpsertRequest;
import cool.houge.mahu.admin.oas.vo.SysDictUpsertRequest;
import cool.houge.mahu.admin.sys.service.DictService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.regex.Pattern;
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
        var dictTypeId = dictTypeId(request);
        var vo = request.content().as(SysDictUpsertRequest.class);
        validate(vo);

        var bean = beanMapper.toDict(vo);
        dictService.save(dictTypeId, bean);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void createSysDictType(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysDictTypeUpsertRequest.class);
        validate(vo);
        validateDataValue(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void deleteSysDictType(ServerRequest request, ServerResponse response) {
        var dictTypeId = dictTypeId(request);
        dictService.deleteById(dictTypeId);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysDictType(ServerRequest request, ServerResponse response) {
        var dictTypeId = dictTypeId(request);

        var bean = dictService.findById(dictTypeId);
        var rs = beanMapper.toSysDictTypeResponse(bean);
        response.send(rs);
    }

    @Override
    public void pageSysDictType(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = dictService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toSysDictTypeResponse);
        response.send(rs);
    }

    @Override
    public void updateSysDictType(ServerRequest request, ServerResponse response) {
        var dictTypeId = dictTypeId(request);
        var vo = request.content().as(SysDictTypeUpsertRequest.class).setId(dictTypeId);
        validate(vo);
        validateDataValue(vo);

        var bean = beanMapper.toDictType(vo);
        dictService.update(bean);
        response.status(NO_CONTENT_204).send();
    }

    String dictTypeId(ServerRequest request) {
        return pathString(request, "type_id");
    }

    void validateDataValue(SysDictTypeUpsertRequest bean) {
        if (Strings.isNullOrEmpty(bean.getValueRegex())
                || bean.getData() == null
                || bean.getData().isEmpty()) {
            return;
        }

        var p = Pattern.compile(bean.getValueRegex());
        for (SysDictUpsertRequest item : bean.getData()) {
            var m = p.matcher(item.getValue());
            if (!m.matches()) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "字典值[%s]不符合规则", item.getValue());
            }
        }
    }
}
