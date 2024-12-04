package cool.houge.mahu.service;

import lombok.Data;

///
/// @author ZY (kzou227@qq.com)
@Data
public class TokenResult {

    private long expiresIn;
    private String accessToken;
    private String refreshToken;
}
