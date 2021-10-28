import {
  Container,
  ProjectNameCSS,
  ProjectPeriods,
  ProjectBody,
  ProjectInfo,
  ProjectType,
  ProjectContentCSS,
  ProjectTypeCSS,
  ProjectDateCSS,
  ProjectImage,
  TagListWrapper,
  TagItemCSS,
  ProjectDateSeparator,
  ProjectDateText,
} from "./PortfolioProjectSection.style";
import Chip from "../@shared/Chip/Chip";
import PortfolioTextEditor from "../PortfolioTextEditor/PortfolioTextEditor";
import DropDown, { DropDownItem } from "../@shared/DropDown/DropDown";
import DateInput from "../@shared/DateInput/DateInput";
import usePortfolioProjectSection from "../../hooks/service/usePortfolioProject";
import { PortfolioProject } from "../../@types";
import { PLACE_HOLDER } from "../../constants/placeholder";

export interface Props {
  project: PortfolioProject;
  isEditable: boolean;
  setProject?: (project: PortfolioProject) => void;
}

const PortfolioProjectSection = ({ project, isEditable, setProject }: Props) => {
  const { deleteTag, updateContent, updateEndDate, updateName, updateStartDate, updateType } =
    usePortfolioProjectSection(project, setProject);
  const projectType = project.type === "team" ? "팀 프로젝트" : "개인 프로젝트";
  const dropDownItems: DropDownItem[] = [
    {
      text: "팀 프로젝트",
      onClick: () => updateType("team"),
    },
    {
      text: "개인 프로젝트",
      onClick: () => updateType("personal"),
    },
  ];

  const tagList = project.tags.map((tag) =>
    isEditable ? (
      <Chip key={tag} cssProp={TagItemCSS} onDelete={() => deleteTag(tag)}>
        {tag}
      </Chip>
    ) : (
      <Chip key={tag} cssProp={TagItemCSS}>
        {tag}
      </Chip>
    )
  );

  const handleUpdateContent: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateContent(event.currentTarget.value);
  };

  const handleUpdateStartDate: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    updateStartDate(event.currentTarget.value);
  };

  const handleUpdateEndDate: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    updateEndDate(event.currentTarget.value);
  };

  const handleUpdateName: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateName(event.currentTarget.value);
  };

  return (
    <Container>
      <ProjectPeriods>
        {isEditable ? (
          <>
            <DateInput cssProp={ProjectDateCSS} value={project.startDate} onChange={handleUpdateStartDate} />
            <ProjectDateSeparator>~</ProjectDateSeparator>
            <DateInput cssProp={ProjectDateCSS} value={project.endDate} onChange={handleUpdateEndDate} />
          </>
        ) : (
          <ProjectDateText>
            {project.startDate} ~ {project.endDate}
          </ProjectDateText>
        )}
      </ProjectPeriods>
      {isEditable ? (
        <DropDown items={dropDownItems} cssProp={ProjectTypeCSS}>
          {projectType}
        </DropDown>
      ) : (
        <ProjectType>{projectType}</ProjectType>
      )}
      <PortfolioTextEditor
        disabled={!isEditable}
        cssProp={ProjectNameCSS}
        value={project.name}
        onChange={handleUpdateName}
        placeholder={PLACE_HOLDER.PROJECT_NAME}
        autoGrow
      />
      <ProjectBody>
        <ProjectImage src={project.imageUrl} />
        <ProjectInfo>
          <PortfolioTextEditor
            cssProp={ProjectContentCSS}
            value={project.content}
            onChange={handleUpdateContent}
            disabled={!isEditable}
            placeholder={PLACE_HOLDER.PROJECT_DESCRIPTION}
            autoGrow
          />
          <TagListWrapper>{tagList}</TagListWrapper>
        </ProjectInfo>
      </ProjectBody>
    </Container>
  );
};

export default PortfolioProjectSection;
