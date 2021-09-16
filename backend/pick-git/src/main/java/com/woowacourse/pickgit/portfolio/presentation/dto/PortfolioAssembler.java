package com.woowacourse.pickgit.portfolio.presentation.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.application.dto.request.ContactRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.DescriptionRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ItemRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ProjectRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.SectionRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.TagRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ContactResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.DescriptionResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ItemResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ProjectResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.SectionResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.TagResponseDto;
import com.woowacourse.pickgit.portfolio.domain.section.item.Description;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ContactResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.DescriptionResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ItemResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ProjectResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.SectionResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.TagResponse;
import java.util.List;

public class PortfolioAssembler {

    public static PortfolioRequestDto of(PortfolioRequest portfolioRequest) {
        List<ContactRequestDto> contactRequestDtos = portfolioRequest.getContacts().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        List<ProjectRequestDto> projectRequestDtos = portfolioRequest.getProjects().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        List<SectionRequestDto> sectionRequestDtos = portfolioRequest.getSections().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new PortfolioRequestDto(
            portfolioRequest.getId(),
            portfolioRequest.isProfileImageShown(),
            portfolioRequest.getProfileImageUrl(),
            portfolioRequest.getIntroduction(),
            contactRequestDtos,
            projectRequestDtos,
            sectionRequestDtos
        );
    }

    private static ContactRequestDto of(ContactRequest contactRequest) {
        return new ContactRequestDto(
            contactRequest.getId(),
            contactRequest.getCategory(),
            contactRequest.getValue()
        );
    }

    private static ProjectRequestDto of(ProjectRequest projectRequest) {
        List<TagRequestDto> tagRequestDtos = projectRequest.getTags().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new ProjectRequestDto(
            projectRequest.getId(),
            projectRequest.getName(),
            projectRequest.getStartDate(),
            projectRequest.getEndDate(),
            projectRequest.getType(),
            projectRequest.getImageUrl(),
            projectRequest.getContent(),
            tagRequestDtos
        );
    }

    private static TagRequestDto of(TagRequest tagRequest) {
        return new TagRequestDto(
            tagRequest.getId(),
            tagRequest.getName()
        );
    }

    private static SectionRequestDto of(SectionRequest sectionRequest) {
        List<ItemRequestDto> itemRequestDtos = sectionRequest.getItems().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new SectionRequestDto(
            sectionRequest.getId(),
            sectionRequest.getName(),
            itemRequestDtos
        );
    }

    private static ItemRequestDto of(ItemRequest itemRequest) {
        List<DescriptionRequestDto> descriptionRequestDtos = itemRequest.getDescriptions().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getCategory(),
            descriptionRequestDtos
        );
    }

    private static DescriptionRequestDto of(DescriptionRequest descriptionRequest) {
        return new DescriptionRequestDto(
            descriptionRequest.getId(),
            descriptionRequest.getValue()
        );
    }

    public static PortfolioResponse of(PortfolioResponseDto responseDto) {
        List<ContactResponse> contactResponses = responseDto.getContacts().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        List<ProjectResponse> projectResponses = responseDto.getProjects().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        List<SectionResponse> sectionResponses = responseDto.getSections().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new PortfolioResponse(
            responseDto.getId(),
            responseDto.isProfileImageShown(),
            responseDto.getProfileImageUrl(),
            responseDto.getIntroduction(),
            contactResponses,
            projectResponses,
            sectionResponses
        );
    }

    private static ContactResponse of(ContactResponseDto contactResponseDto) {
        return new ContactResponse(
            contactResponseDto.getId(),
            contactResponseDto.getCategory(),
            contactResponseDto.getValue()
        );
    }

    private static ProjectResponse of(ProjectResponseDto projectResponseDto) {
        List<TagResponse> tagResponses = projectResponseDto.getTags().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new ProjectResponse(
            projectResponseDto.getId(),
            projectResponseDto.getName(),
            projectResponseDto.getStartDate(),
            projectResponseDto.getEndDate(),
            projectResponseDto.getType(),
            projectResponseDto.getImageUrl(),
            projectResponseDto.getContent(),
            tagResponses
        );
    }

    private static TagResponse of(TagResponseDto tagResponseDto) {
        return new TagResponse(
            tagResponseDto.getId(),
            tagResponseDto.getName()
        );
    }

    private static SectionResponse of(SectionResponseDto sectionResponseDto) {
        List<ItemResponse> itemResponses = sectionResponseDto.getItems().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new SectionResponse(
            sectionResponseDto.getId(),
            sectionResponseDto.getName(),
            itemResponses
        );
    }

    private static ItemResponse of(ItemResponseDto itemResponseDto) {
        List<DescriptionResponse> descriptionResponses = itemResponseDto.getDescriptions().stream()
            .map(PortfolioAssembler::of)
            .collect(toList());

        return new ItemResponse(
            itemResponseDto.getId(),
            itemResponseDto.getCategory(),
            descriptionResponses
        );
    }

    private static DescriptionResponse of(DescriptionResponseDto descriptionResponseDto) {
        return new DescriptionResponse(
            descriptionResponseDto.getId(),
            descriptionResponseDto.getValue()
        );
    }
}
