package com.nivuskorea.procurement.service.openApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service

public class OpenApiService {


    private final WebClient.Builder webClientBuilder;

    public OpenApiService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> fetchData(String baseUrl, String serviceKey, Map<String, String> queryParams) {
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();

        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.queryParam("serviceKey", serviceKey);
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(String.class);
    }
}
