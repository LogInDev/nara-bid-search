package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens", schema = "procurement")
public class AuthToken extends BaseEntity {

    @Id
    @Column(name="token_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_token_seq_gen")
    @SequenceGenerator(name = "auth_token_seq_gen", sequenceName = "procurement.auth_token_seq", allocationSize = 1)
    private Long id;

    private String refreshToken;
    private LocalDateTime expiresIn;

    public AuthToken() {
    }

    public AuthToken(String encryptedRefreshToken, long expiresInSeconds) {
        this.refreshToken = encryptedRefreshToken;
        this.expiresIn = LocalDateTime.now().plusSeconds(expiresInSeconds);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
