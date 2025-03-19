package com.nivuskorea.procurement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthTokenRequest {
    private String refresh_token;
    private long regresh_token_expires_in;
}
