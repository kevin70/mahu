package cool.houge.mahu.admin.bean;

import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import cool.houge.mahu.entity.system.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/// 对象映射
///
/// @author ZY (kzou227@qq.com)
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GeneralBeanMapper {

    @Mapping(target = "uid", source = "id")
    void map(@MappingTarget Profile target, Employee bean);

    @Mapping(target = "data", ignore = true)
    void map(@MappingTarget DictType target, DictType bean);

    @Mapping(target = "dictType", ignore = true)
    void map(@MappingTarget DictData target, DictData bean);
}
