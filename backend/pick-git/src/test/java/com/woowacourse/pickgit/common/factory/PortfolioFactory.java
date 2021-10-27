package com.woowacourse.pickgit.common.factory;

import static java.time.Month.OCTOBER;

import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioResponse mockPortfolioResponse(String name) {
        return PortfolioResponse.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("https://github.com/testImage.jpg")
            .introduction("testDescription")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioResponseDto mockPortfolioResponseDto(String name) {
        return PortfolioResponseDto.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("https://github.com/testImage.jpg")
            .introduction("testDescription")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewProfileAndIntroduction(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }
}
