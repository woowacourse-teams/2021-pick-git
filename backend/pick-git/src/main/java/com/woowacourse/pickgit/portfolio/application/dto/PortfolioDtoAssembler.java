package com.woowacourse.pickgit.portfolio.application.dto;

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
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.contact.Contact;
import com.woowacourse.pickgit.portfolio.domain.contact.Contacts;
import com.woowacourse.pickgit.portfolio.domain.project.Project;
import com.woowacourse.pickgit.portfolio.domain.project.ProjectTag;
import com.woowacourse.pickgit.portfolio.domain.project.Projects;
import com.woowacourse.pickgit.portfolio.domain.section.Section;
import com.woowacourse.pickgit.portfolio.domain.section.Sections;
import com.woowacourse.pickgit.portfolio.domain.section.item.Description;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import com.woowacourse.pickgit.tag.domain.Tag;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class PortfolioDtoAssembler {

    @PersistenceContext
    private final EntityManager entityManager;

    public PortfolioDtoAssembler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Portfolio toPortfolio(PortfolioRequestDto portfolioRequestDto) {
        return new Portfolio(
            portfolioRequestDto.getId(),
            portfolioRequestDto.isProfileImageShown(),
            portfolioRequestDto.getProfileImageUrl(),
            portfolioRequestDto.getIntroduction(),
            toContacts(portfolioRequestDto.getContacts()),
            toProjects(portfolioRequestDto.getProjects()),
            toSections(portfolioRequestDto.getSections())
        );
    }

    private Contacts toContacts(List<ContactRequestDto> contactRequestsDto) {
        List<Contact> contacts = contactRequestsDto.stream()
            .map(this::toContact)
            .collect(toList());

        return new Contacts(contacts);
    }

    private Contact toContact(ContactRequestDto contactRequestDto) {
        return new Contact(
            contactRequestDto.getId(),
            contactRequestDto.getCategory(),
            contactRequestDto.getValue()
        );
    }

    private Projects toProjects(List<ProjectRequestDto> projectRequestsDto) {
        List<Project> projects = projectRequestsDto.stream()
            .map(this::toProject)
            .collect(toList());

        return new Projects(projects);
    }

    private Project toProject(ProjectRequestDto projectRequestDto) {
        List<ProjectTag> tags = projectRequestDto.getTags().stream()
            .map(this::toTag)
            .collect(toList());

        return new Project(
            projectRequestDto.getId(),
            projectRequestDto.getName(),
            projectRequestDto.getStartDate(),
            projectRequestDto.getEndDate(),
            projectRequestDto.getType(),
            projectRequestDto.getImageUrl(),
            projectRequestDto.getContent(),
            tags
        );
    }

    private ProjectTag toTag(TagRequestDto tagRequestDto) {
        Tag tag = entityManager.getReference(Tag.class, tagRequestDto.getId());

        return new ProjectTag(
            tagRequestDto.getId(),
            tag
        );
    }

    private Sections toSections(List<SectionRequestDto> sectionRequestDtos) {
        List<Section> sections = sectionRequestDtos.stream()
            .map(this::toSection)
            .collect(toList());

        return new Sections(sections);
    }

    private Section toSection(SectionRequestDto sectionRequestDto) {
        List<Item> items = sectionRequestDto.getItems().stream()
            .map(this::toItem)
            .collect(toList());

        return new Section(
            sectionRequestDto.getId(),
            sectionRequestDto.getName(),
            items
        );
    }

    private Item toItem(ItemRequestDto itemRequestDto) {
        List<Description> descriptions = itemRequestDto.getDescriptions().stream()
            .map(this::toDescription)
            .collect(toList());

        return new Item(
            itemRequestDto.getId(),
            itemRequestDto.getCategory(),
            descriptions
        );
    }

    private Description toDescription(DescriptionRequestDto descriptionRequestDto) {
        return new Description(
            descriptionRequestDto.getId(),
            descriptionRequestDto.getValue()
        );
    }

    public PortfolioResponseDto toPortfolioResponseDto(Portfolio portfolio) {
        return new PortfolioResponseDto(
            portfolio.getId(),
            portfolio.isProfileImageShown(),
            portfolio.getProfileImageUrl(),
            portfolio.getIntroduction(),
            toContactResponsesDto(portfolio.getContacts()),
            toProjectResponsesDto(portfolio.getProjects()),
            toSectionResponsesDto(portfolio.getSections())
        );
    }

    private List<ContactResponseDto> toContactResponsesDto(Contacts contacts) {
        return contacts.getValues().stream()
            .map(this::toContactResponseDto)
            .collect(toList());
    }

    private ContactResponseDto toContactResponseDto(Contact contact) {
        return new ContactResponseDto(
            contact.getId(),
            contact.getCategory(),
            contact.getValue()
        );
    }

    private List<ProjectResponseDto> toProjectResponsesDto(Projects projects) {
        return projects.getValues().stream()
            .map(this::toProjectResponseDto)
            .collect(toList());
    }

    private ProjectResponseDto toProjectResponseDto(Project project) {
        List<TagResponseDto> tags = project.getTags().stream()
            .map(this::toTagResponseDto)
            .collect(toList());

        return new ProjectResponseDto(
            project.getId(),
            project.getName(),
            project.getStartDate(),
            project.getEndDate(),
            project.getType().getValue(),
            project.getImageUrl(),
            project.getContent(),
            tags
        );
    }

    private TagResponseDto toTagResponseDto(ProjectTag tag) {
        return new TagResponseDto(
            tag.getId(),
            tag.getName()
        );
    }

    private List<SectionResponseDto> toSectionResponsesDto(Sections sections) {
        return sections.getValues().stream()
            .map(this::toSectionResponseDto)
            .collect(toList());
    }

    private SectionResponseDto toSectionResponseDto(Section section) {
        List<ItemResponseDto> items = section.getItems().stream()
            .map(this::toItemResponseDto)
            .collect(toList());

        return new SectionResponseDto(
            section.getId(),
            section.getName(),
            items
        );
    }

    private ItemResponseDto toItemResponseDto(Item item) {
        List<DescriptionResponseDto> descriptions = item.getDescriptions().stream()
            .map(this::toDescriptionResponseDto)
            .collect(toList());

        return new ItemResponseDto(
            item.getId(),
            item.getCategory(),
            descriptions
        );
    }

    private DescriptionResponseDto toDescriptionResponseDto(Description description) {
        return new DescriptionResponseDto(
            description.getId(),
            description.getValue()
        );
    }
}
