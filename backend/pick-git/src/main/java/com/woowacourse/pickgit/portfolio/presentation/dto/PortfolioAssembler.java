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
import java.util.List;

public class PortfolioAssembler {

    private PortfolioAssembler() {
    }

    public static PortfolioRequestDto toPortfolioRequestDto(PortfolioRequest portfolioRequest) {
        List<ContactRequestDto> contactRequestsDto = portfolioRequest.getContacts().stream()
            .map(PortfolioAssembler::toContactRequestDto)
            .collect(toList());

        List<ProjectRequestDto> projectRequestsDto = portfolioRequest.getProjects().stream()
            .map(PortfolioAssembler::toProjectRequestDto)
            .collect(toList());

        List<SectionRequestDto> sectionRequestsDto = portfolioRequest.getSections().stream()
            .map(PortfolioAssembler::toSectionRequestDto)
            .collect(toList());

        return new PortfolioRequestDto(
            portfolioRequest.getId(),
            portfolioRequest.getName(),
            portfolioRequest.isProfileImageShown(),
            portfolioRequest.getProfileImageUrl(),
            portfolioRequest.getIntroduction(),
            portfolioRequest.getCreatedAt(),
            portfolioRequest.getUpdatedAt(),
            contactRequestsDto,
            projectRequestsDto,
            sectionRequestsDto
        );
    }

    private static ContactRequestDto toContactRequestDto(ContactRequest contactRequest) {
        return new ContactRequestDto(
            contactRequest.getId(),
            contactRequest.getCategory(),
            contactRequest.getValue()
        );
    }

    private static ProjectRequestDto toProjectRequestDto(ProjectRequest projectRequest) {
        List<TagRequestDto> tagRequestsDto = projectRequest.getTags().stream()
            .map(PortfolioAssembler::toTagRequestDto)
            .collect(toList());

        return new ProjectRequestDto(
            projectRequest.getId(),
            projectRequest.getName(),
            projectRequest.getStartDate(),
            projectRequest.getEndDate(),
            projectRequest.getType(),
            projectRequest.getImageUrl(),
            projectRequest.getContent(),
            tagRequestsDto
        );
    }

    private static TagRequestDto toTagRequestDto(TagRequest tagRequest) {
        return new TagRequestDto(
            tagRequest.getName()
        );
    }

    private static SectionRequestDto toSectionRequestDto(SectionRequest sectionRequest) {
        List<ItemRequestDto> itemRequestsDto = sectionRequest.getItems().stream()
            .map(PortfolioAssembler::toItemRequestDto)
            .collect(toList());

        return new SectionRequestDto(
            sectionRequest.getId(),
            sectionRequest.getName(),
            itemRequestsDto
        );
    }

    private static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<DescriptionRequestDto> descriptionRequestsDto = itemRequest.getDescriptions().stream()
            .map(PortfolioAssembler::toDescriptionRequestDto)
            .collect(toList());

        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getCategory(),
            descriptionRequestsDto
        );
    }

    private static DescriptionRequestDto toDescriptionRequestDto(
        DescriptionRequest descriptionRequest
    ) {
        return new DescriptionRequestDto(
            descriptionRequest.getId(),
            descriptionRequest.getValue()
        );
    }

    public static PortfolioResponse toPortfolioResponse(PortfolioResponseDto responseDto) {
        List<ContactResponse> contactResponses = responseDto.getContacts().stream()
            .map(PortfolioAssembler::toContactResponse)
            .collect(toList());

        List<ProjectResponse> projectResponses = responseDto.getProjects().stream()
            .map(PortfolioAssembler::toProjectResponse)
            .collect(toList());

        List<SectionResponse> sectionResponses = responseDto.getSections().stream()
            .map(PortfolioAssembler::toSectionResponse)
            .collect(toList());

        return new PortfolioResponse(
            responseDto.getId(),
            responseDto.getName(),
            responseDto.isProfileImageShown(),
            responseDto.getProfileImageUrl(),
            responseDto.getIntroduction(),
            responseDto.getCreatedAt(),
            responseDto.getUpdatedAt(),
            contactResponses,
            projectResponses,
            sectionResponses
        );
    }

    private static ContactResponse toContactResponse(ContactResponseDto contactResponseDto) {
        return new ContactResponse(
            contactResponseDto.getId(),
            contactResponseDto.getCategory(),
            contactResponseDto.getValue()
        );
    }

    private static ProjectResponse toProjectResponse(ProjectResponseDto projectResponseDto) {
        List<String> tagResponses = projectResponseDto.getTags();

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

    private static SectionResponse toSectionResponse(SectionResponseDto sectionResponseDto) {
        List<ItemResponse> itemResponses = sectionResponseDto.getItems().stream()
            .map(PortfolioAssembler::toItemResponse)
            .collect(toList());

        return new SectionResponse(
            sectionResponseDto.getId(),
            sectionResponseDto.getName(),
            itemResponses
        );
    }

    private static ItemResponse toItemResponse(ItemResponseDto itemResponseDto) {
        List<DescriptionResponse> descriptionResponses = itemResponseDto.getDescriptions().stream()
            .map(PortfolioAssembler::toDescriptionResponse)
            .collect(toList());

        return new ItemResponse(
            itemResponseDto.getId(),
            itemResponseDto.getCategory(),
            descriptionResponses
        );
    }

    private static DescriptionResponse toDescriptionResponse(
        DescriptionResponseDto descriptionResponseDto
    ) {
        return new DescriptionResponse(
            descriptionResponseDto.getId(),
            descriptionResponseDto.getValue()
        );
    }
}
