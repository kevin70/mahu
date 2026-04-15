package cool.houge.mahu.admin.bean;

import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.AuthClient;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.entity.sys.Role;
import cool.houge.mahu.model.result.AdminProfileResult;
import io.helidon.service.registry.Service.Contract;
import io.helidon.service.registry.Service.Singleton;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/// 实体对象映射
///
/// @author ZY (kzou227@qq.com)
@Contract
@AnnotateWith(Singleton.class)
@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EntityBeanMapper {

    @Mapping(target = "adminId", source = "id")
    void map(@MappingTarget AdminProfileResult target, Admin bean);

    @Mapping(target = "data", ignore = true)
    void map(@MappingTarget DictGroup target, DictGroup bean);

    @Mapping(target = "dc", ignore = true)
    void map(@MappingTarget Dict target, Dict bean);

    void map(@MappingTarget Role target, Role bean);

    void map(@MappingTarget AuthClient target, AuthClient bean);

    void map(@MappingTarget FeatureFlag target, FeatureFlag bean);
}
