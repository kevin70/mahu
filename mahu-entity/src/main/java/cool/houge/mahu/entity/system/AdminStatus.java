package cool.houge.mahu.entity.system;

import cool.houge.mahu.CodedEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/// 管理员状态
///
/// @author ZY (kzou227@qq.com)
public enum AdminStatus implements CodedEnum {
    /// 活跃的
    ACTIVE(22),
    /// 禁用的
    ///
    /// _是由管理员手动禁用_
    DISABLED(76),
    ;

    private final int code;

    AdminStatus(int code) {
        this.code = code;
    }

    @DbEnumValue(storage = DbEnumType.INTEGER)
    @Override
    public int getCode() {
        return code;
    }
}
