package cool.houge.mahu.admin.mapping;

import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.oas.vo.LoginTokenRequest;
import cool.houge.mahu.admin.oas.vo.LoginTokenResponse;
import cool.houge.mahu.admin.oas.vo.MeNotificationPollResponse;
import cool.houge.mahu.admin.oas.vo.MeNotificationResponse;
import cool.houge.mahu.admin.oas.vo.MePasswordUpdateRequest;
import cool.houge.mahu.admin.oas.vo.MeProfileResponse;
import cool.houge.mahu.admin.oas.vo.MeProfileUpdateRequest;
import cool.houge.mahu.admin.oas.vo.PublicDictGroupResponse;
import cool.houge.mahu.admin.oas.vo.PublicDictResponse;
import cool.houge.mahu.admin.oas.vo.TokenPasswordForm;
import cool.houge.mahu.admin.oas.vo.TokenRefreshTokenForm;
import cool.houge.mahu.admin.sys.dto.MeNotificationPollResult;
import cool.houge.mahu.admin.sys.dto.TokenPayload;
import cool.houge.mahu.admin.sys.dto.TokenResult;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.AdminNotification;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

///
/// @author ZY (kzou227@qq.com)
@AnnotateWith(Service.Singleton.class)
@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VoBeanMapper extends TopBeanMapper {

    default OffsetDateTime map(Instant v) {
        return v == null ? null : v.atOffset(ZoneOffset.UTC);
    }

    @Mapping(target = "data", conditionExpression = "java(includeData)")
    PublicDictGroupResponse toPublicDictGroupResponse(DictGroup bean, boolean includeData);

    PublicDictResponse toPublicDictDataResponse(Dict bean);

    TokenPasswordForm toTokenPasswordForm(LoginTokenRequest bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(LoginTokenRequest bean);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenPasswordForm bean, cool.houge.mahu.config.GrantTypes type);

    @Mapping(target = "grantType", source = "type")
    TokenPayload toTokenPayload(TokenRefreshTokenForm bean, cool.houge.mahu.config.GrantTypes type);

    LoginTokenResponse toLoginTokenResponse(TokenResult bean);

    Admin toAdmin(MePasswordUpdateRequest bean);

    Admin toAdmin(MeProfileUpdateRequest bean);

    MeProfileResponse toMeProfileResponse(Profile bean);

    MeNotificationResponse toMeNotificationResponse(AdminNotification bean);

    MeNotificationPollResponse toMeNotificationPollResponse(MeNotificationPollResult result);
}
