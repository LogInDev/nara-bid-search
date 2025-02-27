package com.nivuskorea.procurement.service.openApi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api.open")
public class OpenApiProperties {
    private String bid_url;
    private String bid_key;
    private String procurement_url;
    private String procurement_key;

}
