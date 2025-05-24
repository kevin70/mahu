package cool.houge.mahu.admin.internal;

import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.oas.model.AdminResponse;
import cool.houge.mahu.admin.oas.model.BrandResponse;
import cool.houge.mahu.admin.oas.model.ClientResponse;
import cool.houge.mahu.admin.oas.model.DepartmentResponse;
import cool.houge.mahu.admin.oas.model.DictResponse;
import cool.houge.mahu.admin.oas.model.ListPMartCategories200ResponseInner;
import cool.houge.mahu.admin.oas.model.LoginRequest;
import cool.houge.mahu.admin.oas.model.MakeOssDirectUploadRequest;
import cool.houge.mahu.admin.oas.model.MakeOssDirectUploadResponse;
import cool.houge.mahu.admin.oas.model.MartAttributeResponse;
import cool.houge.mahu.admin.oas.model.MartAttributeValueResponse;
import cool.houge.mahu.admin.oas.model.MartCategoryResponse;
import cool.houge.mahu.admin.oas.model.MartProductResponse;
import cool.houge.mahu.admin.oas.model.MartProductResponseAttributesInner;
import cool.houge.mahu.admin.oas.model.MartProductResponseVariantsInnerAttributesInner;
import cool.houge.mahu.admin.oas.model.MeProfileResponse;
import cool.houge.mahu.admin.oas.model.ProductTypeEnum;
import cool.houge.mahu.admin.oas.model.PublicDictDataResponse;
import cool.houge.mahu.admin.oas.model.PublicDictResponse;
import cool.houge.mahu.admin.oas.model.RoleResponse;
import cool.houge.mahu.admin.oas.model.ScheduledTaskExecutionResponse;
import cool.houge.mahu.admin.oas.model.ScheduledTaskResponse;
import cool.houge.mahu.admin.oas.model.ShopAssetResponse;
import cool.houge.mahu.admin.oas.model.ShopResponse;
import cool.houge.mahu.admin.oas.model.TokenPasswordForm;
import cool.houge.mahu.admin.oas.model.TokenRefreshTokenForm;
import cool.houge.mahu.admin.oas.model.TokenResponse;
import cool.houge.mahu.admin.oas.model.UpdateMartProductStatusRequest;
import cool.houge.mahu.admin.oas.model.UpdateMartProductStatusRequestVariantsInner;
import cool.houge.mahu.admin.oas.model.UpdateMePasswordRequest;
import cool.houge.mahu.admin.oas.model.UpdateMeProfileRequest;
import cool.houge.mahu.admin.oas.model.UpsertAdminRequest;
import cool.houge.mahu.admin.oas.model.UpsertBrandRequest;
import cool.houge.mahu.admin.oas.model.UpsertClientRequest;
import cool.houge.mahu.admin.oas.model.UpsertDepartmentRequest;
import cool.houge.mahu.admin.oas.model.UpsertDictRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartAttributeRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartAttributeValueRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartCategoryRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartProductRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartProductRequestAttributesInner;
import cool.houge.mahu.admin.oas.model.UpsertMartProductRequestVariantsInnerAttributesInner;
import cool.houge.mahu.admin.oas.model.UpsertRoleRequest;
import cool.houge.mahu.admin.oas.model.UpsertShopRequest;
import cool.houge.mahu.admin.service.MakeOssDirectUploadPayload;
import cool.houge.mahu.admin.service.MakeOssDirectUploadResult;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.dto.TokenResult;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.PageResponse;
import cool.houge.mahu.entity.Brand;
import cool.houge.mahu.entity.log.ScheduledExecutionLog;
import cool.houge.mahu.entity.mart.Asset;
import cool.houge.mahu.entity.mart.Attribute;
import cool.houge.mahu.entity.mart.AttributeValue;
import cool.houge.mahu.entity.mart.Category;
import cool.houge.mahu.entity.mart.Product;
import cool.houge.mahu.entity.mart.ProductAttribute;
import cool.houge.mahu.entity.mart.ProductVariant;
import cool.houge.mahu.entity.mart.ProductVariantAttribute;
import cool.houge.mahu.entity.mart.Shop;
import cool.houge.mahu.entity.system.Admin;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.entity.system.Department;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import cool.houge.mahu.entity.system.Role;
import cool.houge.mahu.entity.system.ScheduledTask;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/// 参考 [mapstruct](https://mapstruct.org/)
///
/// @author ZY (kzou227@qq.com)
@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface VoBeanMapper extends LogBeanMapper {

    default LocalDateTime toLocalDateTime(Instant b) {
        return b != null ? b.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
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

    DictResponse toDictResponse(DictType bean);

    @Mapping(target = "data", ignore = true)
    PublicDictResponse toPublicDictResponseIgnoreData(DictType bean);

    PublicDictResponse toPublicDictResponse(DictType bean);

    PublicDictDataResponse toPublicDictDataResponse(DictData bean);

    TokenPasswordForm toTokenPasswordForm(LoginRequest bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(LoginRequest bean);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenPasswordForm bean, GrantType type);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenRefreshTokenForm bean, GrantType type);

    TokenResponse toTokenResponse(TokenResult bean);

    Admin toAdmin(UpdateMePasswordRequest bean);

    Admin toAdmin(UpdateMeProfileRequest bean);

    MeProfileResponse toGetMeProfileResponse(Profile bean);

    Client toClient(UpsertClientRequest bean);

    ClientResponse toClientResponse(Client bean);

    Role toRole(UpsertRoleRequest bean);

    RoleResponse toRoleResponse(Role bean);

    DepartmentResponse toDepartmentResponse(Department bean);

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
    Admin toAdmin(UpsertAdminRequest bean);

    @Mapping(target = "roleIds", source = "roles", qualifiedByName = "rolesToRoleIds")
    AdminResponse toAdminResponse(Admin bean);

    Brand toBrand(UpsertBrandRequest bean);

    BrandResponse toBrandResponse(Brand bean);

    MakeOssDirectUploadPayload toMakeOssDirectUploadPayload(MakeOssDirectUploadRequest bean);

    MakeOssDirectUploadResponse toMakeOssDirectUploadResponse(MakeOssDirectUploadResult bean);

    Shop toShop(UpsertShopRequest bean);

    ShopResponse toShopResponse(Shop bean);

    ShopAssetResponse toShopAssetResponse(Asset bean);

    Attribute toAttribute(UpsertMartAttributeRequest bean);

    MartAttributeResponse toMartAttributeResponse(Attribute bean);

    @Mapping(source = "attributeId", target = "attribute.id")
    AttributeValue toAttributeValue(Integer attributeId, UpsertMartAttributeValueRequest bean);

    MartAttributeValueResponse toMartAttributeValueResponse(AttributeValue bean);

    Product.Type toProductType(ProductTypeEnum bean);

    Product toProduct(UpsertMartProductRequest bean);

    Product toProduct(UpdateMartProductStatusRequest bean);

    @Mapping(target = "id", source = "productVariantId")
    ProductVariant toProductVariant(UpdateMartProductStatusRequestVariantsInner bean);

    @Mapping(target = "attribute.id", source = "attributeId")
    ProductAttribute toProductAttribute(UpsertMartProductRequestAttributesInner bean);

    @Mapping(target = "attribute.id", source = "attributeId")
    ProductVariantAttribute toProductVariantAttribute(UpsertMartProductRequestVariantsInnerAttributesInner bean);

    MartProductResponse toMartProductResponse(Product bean);

    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    @Mapping(target = "attributeRemark", source = "attribute.remark")
    @Mapping(target = "attributeValueType", source = "attribute.valueType")
    MartProductResponseAttributesInner toMartProductResponseAttributesInner(ProductAttribute bean);

    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "attributeName", source = "attribute.name")
    @Mapping(target = "attributeRemark", source = "attribute.remark")
    @Mapping(target = "attributeValueType", source = "attribute.valueType")
    MartProductResponseVariantsInnerAttributesInner toMartProductResponseVariantsInnerAttributesInner(
            ProductVariantAttribute bean);

    Category toCategory(UpsertMartCategoryRequest bean);

    MartCategoryResponse toGetMartCategoryResponse(Category bean);

    @Mapping(target = "parentId", source = "parent.id")
    ListPMartCategories200ResponseInner toListPMartCategories200ResponseInner(Category bean);

    @Mapping(target = "taskName", source = "taskId.taskName")
    @Mapping(target = "taskInstance", source = "taskId.taskInstance")
    ScheduledTaskResponse toScheduledTaskResponse(ScheduledTask bean);

    @Mapping(target = "taskName", source = "scheduledTask.taskId.taskName")
    @Mapping(target = "taskInstance", source = "scheduledTask.taskId.taskInstance")
    ScheduledTaskExecutionResponse toScheduledTaskExecutionResponse(ScheduledExecutionLog bean);

    @Mapping(target = "taskId.taskName", source = "taskName")
    @Mapping(target = "taskId.taskInstance", source = "taskInstance")
    ScheduledTask toScheduledTask(String taskName, String taskInstance);
}
