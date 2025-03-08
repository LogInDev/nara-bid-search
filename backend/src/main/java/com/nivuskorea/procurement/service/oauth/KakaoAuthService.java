package com.nivuskorea.procurement.service.oauth;

import com.nivuskorea.procurement.service.openApi.OpenApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final OpenApiProperties openApiProperties;

    public String refreshAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", openApiProperties.getClient_id());
        params.add("refresh_token", openApiProperties.getRefresh_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(openApiProperties.getToken_url(), HttpMethod.POST, request, Map.class);

        return response.getBody().get("access_token").toString();
    }
}
