package com.woowacourse.pickgit.portfolio.application;

import com.woowacourse.pickgit.exception.portfolio.NoSuchPortfolioException;
import com.woowacourse.pickgit.portfolio.application.dto.PortfolioDtoAssembler;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioDtoAssembler portfolioDtoAssembler;

    public PortfolioService(
        PortfolioRepository portfolioRepository,
        PortfolioDtoAssembler portfolioDtoAssembler
    ) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioDtoAssembler = portfolioDtoAssembler;
    }

    @Transactional
    public PortfolioResponseDto update(
        PortfolioRequestDto portfolioRequestDto
    ) {
        Portfolio portfolio = portfolioRepository.findById(portfolioRequestDto.getId())
            .orElseThrow(NoSuchPortfolioException::new);
        portfolio.update(portfolioDtoAssembler.of(portfolioRequestDto));

        return portfolioDtoAssembler.of(portfolio);
    }
}
