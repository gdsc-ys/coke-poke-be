package com.inshining.poke.domain.repository;

import com.inshining.poke.domain.entity.user.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Integer> {
    Optional<UserRefreshToken> findByUserIdAndReissueCountLessThan(int id, long count);
}
