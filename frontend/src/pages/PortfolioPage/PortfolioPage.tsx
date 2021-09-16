import { useContext, useEffect, useRef, useState } from "react";
import { Redirect } from "react-router-dom";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Avatar from "../../components/@shared/Avatar/Avatar";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import useProfile from "../../services/hooks/useProfile";
import {
  AvatarWrapper,
  Container,
  FullPage,
  UserNameCSS,
  DescriptionCSS,
  DetailInfo,
  ContactWrapper,
  PaginatorWrapper,
  SectionNameCSS,
  ToggleButtonCSS,
  UserAvatarCSS,
  ContactIconCSS,
  DeleteButtonCSS,
} from "./PortfolioPage.style";
import DotPaginator from "../../components/@shared/DotPaginator/DotPaginator";
import { PortfolioProject, Post } from "../../@types";
import ScrollActiveHeader from "../../components/@layout/ScrollActiveHeader/ScrollActiveHeader";
import PortfolioHeader from "../../components/@layout/PortfolioHeader/PortfolioHeader";
import { getScrollYPosition } from "../../utils/layout";
import PortfolioProjectSection from "../../components/PortfolioProjectSection/PortfolioProjectSection";
import PortfolioSection from "../../components/PortfolioSection/PortfolioSection";
import usePortfolioSections from "../../services/hooks/usePortfolioSection";
import SVGIcon from "../../components/@shared/SVGIcon/SVGIcon";
import PortfolioTextEditor from "../../components/PortfolioTextEditor/PortfolioTextEditor";
import { PLACE_HOLDER } from "../../constants/placeholder";
import useUserFeed from "../../services/hooks/useUserFeed";
import usePortfolioProjects from "../../services/hooks/usePortfolioProjects";
import useModal from "../../services/hooks/@common/useModal";
import ModalPortal from "../../components/@layout/Modal/ModalPortal";
import PostSelector from "../../components/PostSelector/PostSelector";
import ToggleButton from "../../components/@shared/ToggleButton/ToggleButton";
import usePortfolioIntro from "../../services/hooks/usePortfolioIntro";
import Button from "../../components/@shared/Button/Button";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";

const PortfolioPage = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [userName] = new URL(window.location.href).search;
  const { currentUsername, isLoggedIn } = useContext(UserContext);
  const isMyProfile = userName === currentUsername;
  const { data: profile, isLoading } = useProfile(isMyProfile, currentUsername);
  const { infinitePostsData, handleIntersect } = useUserFeed(isMyProfile, currentUsername);
  const {
    portfolioSections,
    addBlankPortfolioSection,
    deletePortfolioSection,
    updatePortfolioSectionName,
    setPortfolioSection,
  } = usePortfolioSections();
  const { portfolioProjects, addPortfolioProject, deletePortfolioProject, updatePortfolioProject } =
    usePortfolioProjects();
  const { isModalShown, showModal, hideModal } = useModal(false);
  const { portfolioIntro, setPortfolioIntro, updateIntroName, updateIntroDescription, updateIsProfileShown } =
    usePortfolioIntro(profile?.name, profile?.description);
  const paginationCount = portfolioProjects.length + portfolioSections.length + 1;
  const {
    modalMessage,
    isModalShown: isMessageModalShown,
    isCancelButtonShown,
    showConfirmModal,
    hideMessageModal,
  } = useMessageModal();
  const [deletingSectionType, setDeletingSectionType] = useState<"project" | "custom">();
  const [deletingSectionName, setDeletingSectionName] = useState("");

  const handleSetProject = (prevProjectName: string) => (newProject: PortfolioProject) => {
    updatePortfolioProject(prevProjectName, newProject);
  };

  const handleAddSection = () => {
    addBlankPortfolioSection();
  };

  const handleAddProject = () => {
    showModal();
  };

  const handlePaginate = (index: number) => {
    if (!containerRef.current) {
      return;
    }

    containerRef.current.scrollTo({
      behavior: "smooth",
      top: getScrollYPosition(containerRef.current.children[index], containerRef.current),
    });
  };

  const handleSectionNameUpdate = (prevSectionName: string) => (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    updatePortfolioSectionName(prevSectionName, event.currentTarget.value);
  };

  const handleIntroNameUpdate: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateIntroName(event.currentTarget.value);
  };

  const handleIntroDescriptionUpdate: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateIntroDescription(event.currentTarget.value);
  };

  const handlePostSelect = (post: Post) => {
    const [firstImageUrl] = post.imageUrls;

    addPortfolioProject({
      content: post.content,
      imageUrl: firstImageUrl,
      name: "",
      startDate: "",
      endDate: "",
      tags: post.tags,
      type: "team",
    });

    hideModal();
  };

  const handleDeleteProjectSection = (sectionName: string) => {
    showConfirmModal("정말 삭제하시겠습니까?");
    setDeletingSectionType("project");
    setDeletingSectionName(sectionName);
  };

  const handleDeleteCustomSection = (sectionName: string) => {
    showConfirmModal("정말 삭제하시겠습니까?");
    setDeletingSectionType("custom");
    setDeletingSectionName(sectionName);
  };

  const handleDeleteSectionConfirm = () => {
    if (deletingSectionType === "project") {
      deletePortfolioProject(deletingSectionName);
      hideMessageModal();
      return;
    }

    deletePortfolioSection(deletingSectionName);
    hideMessageModal();
  };

  useEffect(() => {
    if (profile && portfolioIntro.name === "" && portfolioIntro.description === "") {
      setPortfolioIntro({
        ...portfolioIntro,
        name: profile.name,
        description: profile.description,
      });
    }
  }, [profile]);

  if (!isLoggedIn) {
    return <Redirect to={PAGE_URL.HOME} />;
  }

  if (isLoading) {
    return <PageLoading />;
  }

  if (!profile) {
    return <div>이용자 정보를 찾을 수 없습니다.</div>;
  }

  return (
    <>
      <ScrollActiveHeader containerRef={containerRef}>
        <PortfolioHeader onAddPortfolioSection={handleAddSection} onAddPortfolioProject={handleAddProject} />
      </ScrollActiveHeader>
      <Container ref={containerRef}>
        <FullPage isVerticalCenter={true}>
          <ToggleButton
            toggleButtonText="프로필 사진 보이기"
            cssProp={ToggleButtonCSS}
            isToggled={portfolioIntro.isProfileShown}
            onToggle={() => updateIsProfileShown(!portfolioIntro.isProfileShown)}
          />
          <AvatarWrapper>
            {portfolioIntro.isProfileShown && (
              <Avatar diameter="6.5625rem" fontSize="1.5rem" imageUrl={profile.imageUrl} cssProp={UserAvatarCSS} />
            )}
            <PortfolioTextEditor
              cssProp={UserNameCSS}
              value={portfolioIntro.name}
              onChange={handleIntroNameUpdate}
              autoGrow
            />
          </AvatarWrapper>
          <PortfolioTextEditor
            cssProp={DescriptionCSS}
            value={portfolioIntro.description}
            onChange={handleIntroDescriptionUpdate}
            autoGrow
          />
          <ContactWrapper>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="CompanyIcon" />
              {profile.company ? profile.company : "-"}
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="LocationIcon" />
              {profile.location ? profile.location : "-"}
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="GithubDarkIcon" />
              <a href={profile.githubUrl ?? ""}>{profile.githubUrl ? profile.githubUrl : "-"}</a>
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="WebsiteLinkIcon" />
              <a href={profile.website ?? ""}>{profile.website ? profile.website : "-"}</a>
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="TwitterIcon" />
              {profile.twitter ? profile.twitter : "-"}
            </DetailInfo>
          </ContactWrapper>
        </FullPage>
        {portfolioProjects.map((portfolioProject) => (
          <FullPage isVerticalCenter={true}>
            <PortfolioProjectSection project={portfolioProject} setProject={handleSetProject(portfolioProject.name)} />
            <Button
              cssProp={DeleteButtonCSS}
              kind="roundedInline"
              onClick={() => handleDeleteProjectSection(portfolioProject.name)}
            >
              프로젝트 삭제
            </Button>
          </FullPage>
        ))}
        {portfolioSections.map((portfolioSection) => (
          <FullPage>
            <PortfolioTextEditor
              cssProp={SectionNameCSS}
              value={portfolioSection.name}
              onChange={handleSectionNameUpdate(portfolioSection.name)}
              autoGrow={true}
              placeholder={PLACE_HOLDER.SECTION_NAME}
            />
            <PortfolioSection section={portfolioSection} setSection={setPortfolioSection} />
            <Button
              cssProp={DeleteButtonCSS}
              kind="roundedInline"
              onClick={() => handleDeleteCustomSection(portfolioSection.name)}
            >
              섹션 삭제
            </Button>
          </FullPage>
        ))}
        {isModalShown && isLoggedIn && (
          <ModalPortal onClose={hideModal} isCloseButtonShown={true}>
            <PostSelector infinitePostsData={infinitePostsData} onPostSelect={handlePostSelect} />
          </ModalPortal>
        )}
        {isMessageModalShown && isCancelButtonShown && (
          <MessageModalPortal
            heading={modalMessage}
            onConfirm={handleDeleteSectionConfirm}
            onClose={hideMessageModal}
            onCancel={hideMessageModal}
          />
        )}
      </Container>
      <PaginatorWrapper>
        <DotPaginator paginationCount={paginationCount} paginate={handlePaginate} />
      </PaginatorWrapper>
    </>
  );
};

export default PortfolioPage;
