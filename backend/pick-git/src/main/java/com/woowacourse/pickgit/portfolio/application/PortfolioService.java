package com.woowacourse.pickgit.portfolio.application;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.portfolio.NoSuchPortfolioException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.portfolio.application.dto.PortfolioDtoAssembler;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.UserDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.repository.PortfolioRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioDtoAssembler portfolioDtoAssembler;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PortfolioService(
        PortfolioRepository portfolioRepository,
        PortfolioDtoAssembler portfolioDtoAssembler,
        UserRepository userRepository
    ) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioDtoAssembler = portfolioDtoAssembler;
        this.userRepository = userRepository;
    }

    public PortfolioResponseDto read(String username, UserDto userDto) {
        Optional<Portfolio> portfolio = portfolioRepository.findPortfolioByUsername(username);

        if (userDto.isGuest() && portfolio.isEmpty()) {
            throw new NoSuchPortfolioException();
        }

        return portfolioDtoAssembler.toPortfolioResponseDto(
            portfolio.orElseGet(() -> portfolioRepository.save(Portfolio.empty(getUser(username))))
        );
    }

    @Transactional
    public PortfolioResponseDto update(PortfolioRequestDto portfolioRequestDto, UserDto userDto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioRequestDto.getId())
            .orElseThrow(NoSuchPortfolioException::new);
        User user = getUser(userDto.getUsername());

        if (!portfolio.isOwnedBy(user)) {
            throw new UnauthorizedException();
        }

        portfolio.update(portfolioDtoAssembler.toPortfolio(portfolioRequestDto));
        entityManager.detach(portfolio);

        return portfolioDtoAssembler.toPortfolioResponseDto(portfolio);
    }

    private User getUser(String username) {
        return userRepository.findByBasicProfile_Name(username)
            .orElseThrow(UserNotFoundException::new);
    }
}
