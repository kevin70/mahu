package cool.houge.mahu.internal;

import cool.houge.mahu.entity.Region;
import cool.houge.mahu.entity.User;
import cool.houge.mahu.oas.model.*;
import cool.houge.mahu.service.TokenPayload;
import cool.houge.mahu.service.TokenResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/// 参考 [mapstruct](https://mapstruct.org/)
///
/// @author ZY (kzou227@qq.com)
@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface VoBeanMapper {

    GetRegionResponse toGetRegionResponse(Region bean);

    GetDictResponse toGetDictResponse(Dict bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(GetTokenRequest bean);

    TokenPasswordForm toTokenPasswordForm(GetTokenRequest bean);

    TokenWechatXcxForm toTokenWechatXcxForm(GetTokenRequest bean);

    @Mapping(target = "grantType", expression = "java(cool.houge.mahu.common.GrantType.REFRESH_TOKEN)")
    TokenPayload toTokenPayload(TokenRefreshTokenForm bean);

    @Mapping(target = "grantType", expression = "java(cool.houge.mahu.common.GrantType.PASSWORD)")
    TokenPayload toTokenPayload(TokenPasswordForm bean);

    @Mapping(target = "grantType", expression = "java(cool.houge.mahu.common.GrantType.WECHAT_XCX)")
    TokenPayload toTokenPayload(TokenWechatXcxForm bean);

    GetTokenResponse toGetTokenResponse(TokenResult bean);

    @Mapping(target = "uid", source = "id")
    GetMeProfileResponse toGetMeProfileResponse(User bean);

    User toUser(UpdateMeProfileRequest bean);
}
