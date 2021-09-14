package com.woowacourse.pickgit.portfolio.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.portfolio.application.dto.request.ContactRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ProjectRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.SectionRequestDto;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(value = "*")
public class PortfolioController {

    @PutMapping
    public ResponseEntity<Void> update(
        @Authenticated AppUser user,
        @Valid @RequestBody PortfolioRequest request
    ) {


        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(new PortfolioResponse());
    }

    private PortfolioRequestDto createPortfolioRequestDto(PortfolioRequest request) {
        return PortfolioRequestDto.builder()
            .id(request.getId())
            .profileImageShown(request.isProfileImageShown())
            .profileImageUrl(request.getProfileImageUrl())
            .introduction(request.getIntroduction())
            .contacts(createContactRequestsDto(request.getContacts()))
            .projects(createProjectRequestsDto(request.getProjects()))
            .sections(createSectionRequestsDto(request.getSections()))
            .build();
    }

    private List<ContactRequestDto> createContactRequestsDto(List<ContactRequest> requests) {
        return requests.stream()
            .map(ContactRequestDto::of)
            .collect(toList());
    }

    private List<ProjectRequestDto> createProjectRequestsDto(List<ProjectRequest> requests) {
        return requests.stream()
            .map(ProjectRequestDto::of)
            .collect(toList());
    }

    private List<SectionRequestDto> createSectionRequestsDto(List<SectionRequest> requests) {
        return requests.stream()
            .map(SectionRequestDto::of)
            .collect(toList());
    }
}
