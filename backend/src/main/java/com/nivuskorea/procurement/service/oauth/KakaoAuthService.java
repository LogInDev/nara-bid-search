package com.nivuskorea.procurement.service.oauth;

import com.nivuskorea.procurement.config.EncryptionUtil;
import com.nivuskorea.procurement.dto.response.AuthTokenResponse;
import com.nivuskorea.procurement.dto.response.BaseResponse;
import com.nivuskorea.procurement.entity.AuthToken;
import com.nivuskorea.procurement.repository.AuthTokenRepository;
import com.nivuskorea.procurement.service.openApi.OpenApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KakaoAuthService {

    private final OpenApiProperties openApiProperties;
    private final EncryptionUtil encryptionUtil;
    private final AuthTokenRepository authTokenRepository;

    public AuthToken createAuthToken(String refreshToken, long expiresInSeconds) {
        try{
            final String encryptedToken = encryptionUtil.encrypt(refreshToken);
            return new AuthToken(encryptedToken, expiresInSeconds);
        }catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public String getLatestRefreshToken()  {
        try{
            final String refreshToken = authTokenRepository.findTopByOrderByCreatedAtDesc()
                    .map(AuthToken::getRefreshToken)
                    .orElse(null);
            return encryptionUtil.decrypt(refreshToken);
        }catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional
    public void insertRefreshToken(String refreshToken, long expiresInSeconds)  {
        final AuthToken newAuthToken = createAuthToken(refreshToken, expiresInSeconds);
        authTokenRepository.save(newAuthToken);
    }

    public BaseResponse<AuthTokenResponse> refreshAccessToken()  {
        String refreshToken = getLatestRefreshToken();
        if (refreshToken == null) {
            return new BaseResponse<>(-401,"refresh_token 없음", null);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", openApiProperties.getClient_id());
        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(
                    openApiProperties.getToken_url(),
                    HttpMethod.POST,
                    request,
                    Map.class
            );
        } catch (Exception e) {
            log.error("❌ Access Token 재발급 요청 실패: {}", e.getMessage());
            return new BaseResponse<>(-401, "access_token 재발급 요청 실패", null);
        }
        // ✅ 응답이 200이 아닐 경우 -401로 변환
        if (!response.getStatusCode().is2xxSuccessful()) {
            return new BaseResponse<>(-401, "access_token 재발급 실패", null);
        }

        Map<String, Object> responseBody = response.getBody();

        // ✅ 새로운 refresh_token이 있으면 DB에 저장
        final String newRefreshToken = (String) responseBody.get("refresh_token");
        if (newRefreshToken != null) {
            final long refreshTokenExpiresIn = ((Number) responseBody.get("refresh_token_expires_in")).longValue();
            insertRefreshToken(newRefreshToken, refreshTokenExpiresIn);
        }

        final String accessToken = (String) responseBody.get("access_token");

        return new BaseResponse<>(200, "access_token 재발급 성공", new AuthTokenResponse(accessToken));
    }
}
