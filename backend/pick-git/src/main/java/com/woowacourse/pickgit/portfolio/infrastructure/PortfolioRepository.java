package com.woowacourse.pickgit.portfolio.infrastructure;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

}
