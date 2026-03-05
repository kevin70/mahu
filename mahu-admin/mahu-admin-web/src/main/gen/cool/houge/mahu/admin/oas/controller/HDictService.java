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
        rules.post("/sys/dict-groups/{group_id}", authenticate().andAuthorize("SYS_DICT:W"), this::createSysDictData);
        rules.post("/sys/dict-groups", authenticate().andAuthorize("SYS_DICT:W"), this::createSysDictGroup);
        rules.delete("/sys/dict-groups/{group_id}", authenticate().andAuthorize("SYS_DICT:W"), this::deleteSysDictGroup);
        rules.get("/sys/dict-groups/{group_id}", authenticate().andAuthorize("SYS_DICT:R"), this::getSysDictGroup);
        rules.get("/sys/dict-groups", authenticate().andAuthorize("SYS_DICT:R"), this::pageSysDictGroup);
        rules.put("/sys/dict-groups/{group_id}", authenticate().andAuthorize("SYS_DICT:W"), this::updateSysDictGroup);
    }

    ///
    /// `POST /sys/dict-groups/{group_id}` 新增字典数据
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysDictData(ServerRequest request, ServerResponse response);

    ///
    /// `POST /sys/dict-groups` 新建字典分组
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysDictGroup(ServerRequest request, ServerResponse response);

    ///
    /// `DELETE /sys/dict-groups/{group_id}` 删除字典分组
    ///
    /// @param request the server request
    /// @param response the server response
    void deleteSysDictGroup(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/dict-groups/{group_id}` 获取字典分组数据
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysDictGroup(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/dict-groups` 字典分组分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysDictGroup(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/dict-groups/{group_id}` 修改字典分组
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysDictGroup(ServerRequest request, ServerResponse response);

}
