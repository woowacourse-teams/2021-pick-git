package com.woowacourse.pickgit.portfolio.domain.repository;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query("select Portfolio from Portfolio p where p.user.basicProfile.name = :username")
    Optional<Portfolio> findPortfolioByUsername(String username);
}
