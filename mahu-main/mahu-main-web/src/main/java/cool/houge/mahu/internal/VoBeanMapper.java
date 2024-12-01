package cool.houge.mahu.internal;

import cool.houge.mahu.entity.Region;
import cool.houge.mahu.entity.system.Dict;
import cool.houge.mahu.oas.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/// 参考 [mapstruct](https://mapstruct.org/)
///
/// @author ZY (kzou227@qq.com)
@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface VoBeanMapper {

    GetRegionResponse toGetRegionResponse(Region bean);

    GetDictResponse toGetDictResponse(Dict bean);

    TokenRefreshTokenForm toTokenRefreshTokenForm(GetTokenRequest bean);

    TokenWechatXcxForm toTokenWechatXcxForm(GetTokenRequest bean);
}
