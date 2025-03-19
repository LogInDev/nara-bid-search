package com.nivuskorea.procurement.controller;

import com.nivuskorea.procurement.dto.response.AuthTokenRequest;
import com.nivuskorea.procurement.dto.response.AuthTokenResponse;
import com.nivuskorea.procurement.dto.response.BaseResponse;
import com.nivuskorea.procurement.service.oauth.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class KaKaoOauthController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/refresh-token")
    public ResponseEntity<BaseResponse<AuthTokenResponse>> refreshToken() throws Exception {
        // 🔹 access_token 재발급 요청
        final BaseResponse<AuthTokenResponse> authTokenResponseBaseResponse = kakaoAuthService.refreshAccessToken();

        // 🔹 그대로 프론트엔드에 반환
        return ResponseEntity.ok(authTokenResponseBaseResponse);
    }

    @PostMapping("/set-token")
    public ResponseEntity<String> saveRefreshToken(@RequestBody AuthTokenRequest tokens){
        log.info("Saving refresh token");
        log.info("Refresh token: {}", tokens);
        kakaoAuthService.insertRefreshToken(tokens.getRefresh_token(), tokens.getRegresh_token_expires_in());
        return ResponseEntity.ok("ok");
    }
}
