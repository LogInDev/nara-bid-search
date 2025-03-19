package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findTopByOrderByCreatedAtDesc();

}
