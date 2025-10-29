package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.16.0", trigger = "openapi-generator")
public interface HDictService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/sys/dicts/{dict_type_id}", authenticate().andAuthorize("SYS_DICT:W"), this::createSysDictData);
        rules.post("/sys/dicts", authenticate().andAuthorize("SYS_DICT:W"), this::createSysDictType);
        rules.delete("/sys/dicts/{dict_type_id}", authenticate().andAuthorize("SYS_DICT:W"), this::deleteSysDictType);
        rules.get("/sys/dicts/{dict_type_id}", authenticate().andAuthorize("SYS_DICT:R"), this::getSysDictType);
        rules.get("/sys/dicts", authenticate().andAuthorize("SYS_DICT:R"), this::pageSysDictType);
        rules.put("/sys/dicts/{dict_type_id}", authenticate().andAuthorize("SYS_DICT:W"), this::updateSysDictType);
    }

    ///
    /// `POST /sys/dicts/{dict_type_id}` 新增字典数据
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysDictData(ServerRequest request, ServerResponse response);

    ///
    /// `POST /sys/dicts` 新建字典类型
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysDictType(ServerRequest request, ServerResponse response);

    ///
    /// `DELETE /sys/dicts/{dict_type_id}` 删除字典类型
    ///
    /// @param request the server request
    /// @param response the server response
    void deleteSysDictType(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/dicts/{dict_type_id}` 获取字典类型数据
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysDictType(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/dicts` 字典类型分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysDictType(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/dicts/{dict_type_id}` 修改字典类型
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysDictType(ServerRequest request, ServerResponse response);

}
