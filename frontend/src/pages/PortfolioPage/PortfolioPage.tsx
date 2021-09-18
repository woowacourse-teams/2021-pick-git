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
  CloseButtonWrapper,
  SectionNameCSS,
  ToggleButtonCSS,
  UserAvatarCSS,
  ContactIconCSS,
} from "./PortfolioPage.style";
import DotPaginator from "../../components/@shared/DotPaginator/DotPaginator";
import { Portfolio, PortfolioProject, Post } from "../../@types";
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
import usePortfolio from "../../services/hooks/usePortfolio";
import {
  getPortfolioLocalUpdateTime,
  getPortfolioServerUpdateTime,
  setPortfolioServerUpdateTime,
} from "../../storage/storage";

const PortfolioPage = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const username = new URLSearchParams(location.search).get("username") ?? "";
  const { currentUsername, isLoggedIn } = useContext(UserContext);
  const isMyPortfolio = username === currentUsername;
  const {
    portfolio: remotePortfolio,
    isError,
    isLoading: isPortfolioLoading,
    mutateSetPortfolio,
  } = usePortfolio(username);
  const { data: profile, isLoading: isProfileLoading } = useProfile(isMyPortfolio, currentUsername);
  const { infinitePostsData, handleIntersect } = useUserFeed(isMyPortfolio, currentUsername);

  const {
    portfolioSections,
    addBlankPortfolioSection,
    deletePortfolioSection,
    updatePortfolioSectionName,
    setPortfolioSection,
    setPortfolioSections,
  } = usePortfolioSections();

  const {
    portfolioProjects,
    addPortfolioProject,
    deletePortfolioProject,
    updatePortfolioProject,
    setPortfolioProjects,
  } = usePortfolioProjects();

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
      id: null,
      content: post.content,
      imageUrl: firstImageUrl,
      name: "",
      startDate: "",
      endDate: "",
      tags: post.tags.map((tagName) => ({ id: null, name: tagName })),
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

  const handleUploadPortfolio = async () => {
    try {
      const portfolio = {
        id: remotePortfolio?.id ?? null,
        profileImageShown: portfolioIntro.isProfileShown,
        profileImageUrl: profile?.imageUrl ?? "",
        introduction: portfolioIntro.description,
        contacts: portfolioIntro.contacts,
        projects: portfolioProjects,
        sections: portfolioSections,
      };

      await mutateSetPortfolio(portfolio);
      setPortfolioServerUpdateTime(new Date());
    } catch (error) {
      console.error(error);
    }
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

  useEffect(() => {
    const localUpdateTime = getPortfolioLocalUpdateTime();
    const serverUpdateTime = getPortfolioServerUpdateTime();

    if (remotePortfolio && localUpdateTime < serverUpdateTime) {
      const intro = {
        name: username,
        description: remotePortfolio.introduction,
        isProfileShown: remotePortfolio.profileImageShown,
        contacts: [...remotePortfolio.contacts],
      };

      setPortfolioIntro(intro);
      setPortfolioProjects(remotePortfolio.projects);
      setPortfolioSections(remotePortfolio.sections);
    }
  }, [remotePortfolio]);

  if (!isLoggedIn) {
    return <Redirect to={PAGE_URL.HOME} />;
  }

  if (isProfileLoading) {
    return <PageLoading />;
  }

  if (!remotePortfolio && !isMyPortfolio) {
    return <div>포트폴리오를 찾을 수 없습니다</div>;
  }

  return (
    <>
      <ScrollActiveHeader containerRef={containerRef}>
        <PortfolioHeader
          isButtonsShown={isMyPortfolio}
          onAddPortfolioSection={handleAddSection}
          onAddPortfolioProject={handleAddProject}
          onUploadPortfolio={handleUploadPortfolio}
        />
      </ScrollActiveHeader>
      <Container ref={containerRef}>
        <FullPage isVerticalCenter={true}>
          {isMyPortfolio && (
            <ToggleButton
              toggleButtonText="프로필 사진 보이기"
              cssProp={ToggleButtonCSS}
              isToggled={portfolioIntro.isProfileShown}
              onToggle={() => updateIsProfileShown(!portfolioIntro.isProfileShown)}
            />
          )}
          <AvatarWrapper>
            {portfolioIntro.isProfileShown && (
              <Avatar diameter="6.5625rem" fontSize="1.5rem" imageUrl={profile?.imageUrl} cssProp={UserAvatarCSS} />
            )}
            <PortfolioTextEditor
              cssProp={UserNameCSS}
              value={portfolioIntro.name}
              onChange={handleIntroNameUpdate}
              disabled={!isMyPortfolio}
              placeholder={PLACE_HOLDER.INTRO_NAME}
              autoGrow
            />
          </AvatarWrapper>
          <PortfolioTextEditor
            cssProp={DescriptionCSS}
            value={portfolioIntro.description}
            onChange={handleIntroDescriptionUpdate}
            disabled={!isMyPortfolio}
            placeholder={PLACE_HOLDER.INTRO_DESCRIPTION}
            autoGrow
          />
          <ContactWrapper>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="CompanyIcon" />
              {profile?.company ? profile?.company : "-"}
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="LocationIcon" />
              {profile?.location ? profile?.location : "-"}
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="GithubDarkIcon" />
              <a href={profile?.githubUrl ?? ""}>{profile?.githubUrl ? profile?.githubUrl : "-"}</a>
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="WebsiteLinkIcon" />
              <a href={profile?.website ?? ""}>{profile?.website ? profile?.website : "-"}</a>
            </DetailInfo>
            <DetailInfo>
              <SVGIcon cssProp={ContactIconCSS} icon="TwitterIcon" />
              {profile?.twitter ? profile?.twitter : "-"}
            </DetailInfo>
          </ContactWrapper>
        </FullPage>
        {/* TODO: remove index from key prop */}
        {portfolioProjects.map((portfolioProject, i) => (
          <FullPage isVerticalCenter={true} key={i}>
            <PortfolioProjectSection
              isEditable={isMyPortfolio}
              project={portfolioProject}
              setProject={handleSetProject(portfolioProject.name)}
            />
            {isMyPortfolio && (
              <CloseButtonWrapper>
                <Button
                  kind="roundedInline"
                  padding="0.5rem"
                  onClick={() => handleDeleteProjectSection(portfolioProject.name)}
                >
                  <SVGIcon icon="CancelNoCircleIcon" />
                </Button>
              </CloseButtonWrapper>
            )}
          </FullPage>
        ))}
        {/* TODO: remove index from key prop */}
        {portfolioSections.map((portfolioSection, i) => (
          <FullPage key={i}>
            <PortfolioTextEditor
              cssProp={SectionNameCSS}
              value={portfolioSection.name}
              onChange={handleSectionNameUpdate(portfolioSection.name)}
              autoGrow={true}
              placeholder={PLACE_HOLDER.SECTION_NAME}
              disabled={!isMyPortfolio}
            />
            <PortfolioSection isEditable={isMyPortfolio} section={portfolioSection} setSection={setPortfolioSection} />
            {isMyPortfolio && (
              <CloseButtonWrapper>
                <Button
                  kind="roundedInline"
                  padding="0.5rem"
                  onClick={() => handleDeleteCustomSection(portfolioSection.name)}
                >
                  <SVGIcon icon="CancelNoCircleIcon" />
                </Button>
              </CloseButtonWrapper>
            )}
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
