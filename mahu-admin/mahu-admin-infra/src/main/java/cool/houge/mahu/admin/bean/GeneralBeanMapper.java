package cool.houge.mahu.admin.bean;

import cool.houge.mahu.entity.system.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/// 对象映射
///
/// @author ZY (kzou227@qq.com)
@Mapper
public interface GeneralBeanMapper {

    void map(@MappingTarget Profile target, Employee bean);
}
