package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.oas.model.*;
import cool.houge.mahu.admin.service.MakeOssDirectUploadPayload;
import cool.houge.mahu.admin.service.MakeOssDirectUploadResult;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.dto.TokenResult;
import cool.houge.mahu.admin.system.repository.DictTypeRepository;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.PageResponse;
import cool.houge.mahu.entity.Brand;
import cool.houge.mahu.entity.mart.*;
import cool.houge.mahu.entity.system.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.*;
import java.util.List;
import java.util.function.Function;

/// 参考 [mapstruct](https://mapstruct.org/)
///
/// @author ZY (kzou227@qq.com)
@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface VoBeanMapper extends LogBeanMapper {

    default OffsetDateTime map(Instant b) {
        return b != null ? OffsetDateTime.ofInstant(b, ZoneOffset.ofHours(8)) : null;
    }

    default OffsetDateTime map(LocalDateTime b) {
        return b != null ? b.atOffset(ZoneOffset.ofHours(8)) : null;
    }

    default OffsetDateTime map(ZonedDateTime b) {
        return b != null ? b.toOffsetDateTime() : null;
    }

    /// 分页对象映射
    ///
    /// @param list       数据集合
    /// @param totalCount 总记录数
    /// @param fn         映射
    /// @param <T>        数据对象类型
    /// @param <R>        响应对象类型
    default <T, R> PageResponse<R> toPageResponse(List<T> list, Integer totalCount, Function<T, R> fn) {
        var resp = new PageResponse<R>();
        if (totalCount != null && totalCount > 0) {
            resp.setTotalCount(totalCount);
        }
        if (list != null) {
            resp.setItems(list.stream().map(fn).toList());
        }
        return resp;
    }

    DictType toDictType(UpsertDictRequest bean);

    GetSystemDictResponse toGetSystemDictResponse(DictType bean);

    DictResponse toDictResponse(DictType bean);

    @Mapping(target = "data", ignore = true)
    DictResponse toDictResponseIgnoreData(DictType bean);

    DictDataResponse toDictDataResponse(DictData bean);

    TokenPasswordForm toTokenPasswordForm(LoginRequest bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(LoginRequest bean);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenPasswordForm bean, GrantType type);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenRefreshTokenForm bean, GrantType type);

    GetTokenResponse toGetTokenResponse(TokenResult bean);

    Employee toEmployee(UpdateMePasswordRequest bean);

    Employee toEmployee(UpdateMeProfileRequest bean);

    GetMeProfileResponse toGetMeProfileResponse(Profile bean);

    Client toClient(UpsertClientRequest bean);

    GetClientResponse toGetClientResponse(Client bean);

    Role toRole(UpsertRoleRequest bean);

    GetRoleResponse toGetRoleResponse(Role bean);

    GetDepartmentResponse toGetDepartmentResponse(Department bean);

    @Mapping(target = "parent.id", source = "parentId")
    Department toDepartment(UpsertDepartmentRequest bean);

    @Named("roleIdsToRoles")
    default List<Role> roleIdsToRoles(List<Integer> roleIds) {
        if (roleIds == null) return null;
        return roleIds.stream().map(id -> new Role().setId(id)).toList();
    }

    @Named("rolesToRoleIds")
    default List<Integer> rolesToRoleIds(List<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(Role::getId).toList();
    }

    @Mapping(target = "department.id", source = "departmentId")
    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "roleIdsToRoles")
    Employee toEmployee(UpsertEmployeeRequest bean);

    @Mapping(target = "roleIds", source = "roles", qualifiedByName = "rolesToRoleIds")
    GetEmployeeResponse toGetEmployeeResponse(Employee bean);

    Brand toBrand(UpsertBrandRequest bean);

    GetBrandResponse toGetBrandResponse(Brand bean);

    MakeOssDirectUploadPayload toMakeOssDirectUploadPayload(MakeOssDirectUploadRequest bean);

    MakeOssDirectUploadResponse toMakeOssDirectUploadResponse(MakeOssDirectUploadResult bean);

    Shop toShop(UpsertShopRequest bean);

    GetShopResponse toGetShopResponse(Shop bean);

    GetShopAssetResponse toGetShopAssetResponse(Asset bean);

    Attribute toAttribute(UpsertMartAttributeRequest bean);

    GetMartAttributeResponse toGetMartAttributeResponse(Attribute bean);

    @Mapping(source = "attributeId", target = "attribute.id")
    AttributeValue toAttributeValue(Integer attributeId, UpsertMartAttributeValueRequest bean);

    GetMartAttributeValueResponse toGetMartAttributeValueResponse(AttributeValue bean);

    Product.Type toProductType(ProductTypeEnum bean);

    Product toProduct(UpsertMartProductRequest bean);

    Product toProduct(UpdateMartProductStatusRequest bean);

    @Mapping(target = "id", source = "productVariantId")
    ProductVariant toProductVariant(UpdateMartProductStatusRequestVariantsInner bean);

    @Mapping(target = "attribute.id", source = "attributeId")
    ProductAttribute toProductAttribute(UpsertMartProductRequestAttributesInner bean);

    @Mapping(target = "attribute.id", source = "attributeId")
    ProductVariantAttribute toProductVariantAttribute(UpsertMartProductRequestVariantsInnerAttributesInner bean);

    GetMartProductResponse toGetMartProductResponse(Product bean);

    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    @Mapping(target = "attributeRemark", source = "attribute.remark")
    @Mapping(target = "attributeValueType", source = "attribute.valueType")
    GetMartProductResponseAttributesInner toGetMartProductResponseAttributesInner(ProductAttribute bean);

    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    @Mapping(target = "attributeRemark", source = "attribute.remark")
    @Mapping(target = "attributeValueType", source = "attribute.valueType")
    GetMartProductResponseVariantsInnerAttributesInner toGetMartProductResponseVariantsInnerAttributesInner(
            ProductVariantAttribute bean);

    Category toCategory(UpsertMartCategoryRequest bean);

    GetMartCategoryResponse toGetMartCategoryResponse(Category bean);

    MartCategory toMartCategory(Category bean);

    @Mapping(target = "parentId", source = "parent.id")
    ListPMartCategories200ResponseInner toListPMartCategories200ResponseInner(Category bean);

    @Mapping(target = "taskName", source = "taskId.taskName")
    @Mapping(target = "taskInstance", source = "taskId.taskInstance")
    GetSystemScheduledTaskResponse toGetSystemScheduledTaskResponse(ScheduledTask bean);
}
