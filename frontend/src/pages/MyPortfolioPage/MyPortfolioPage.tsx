import { useEffect, useRef, useState } from "react";
import { Redirect } from "react-router-dom";

import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";
import ModalPortal from "../../components/@layout/Modal/ModalPortal";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import PortfolioHeader from "../../components/@layout/PortfolioHeader/PortfolioHeader";
import ScrollActiveHeader from "../../components/@layout/ScrollActiveHeader/ScrollActiveHeader";
import Avatar from "../../components/@shared/Avatar/Avatar";
import Button from "../../components/@shared/Button/Button";
import DotPaginator from "../../components/@shared/DotPaginator/DotPaginator";
import SVGIcon from "../../components/@shared/SVGIcon/SVGIcon";
import ToggleButton from "../../components/@shared/ToggleButton/ToggleButton";
import PageError from "../../components/@shared/PageError/PageError";
import PortfolioProjectSection from "../../components/PortfolioProjectSection/PortfolioProjectSection";
import PortfolioSection from "../../components/PortfolioSection/PortfolioSection";
import PortfolioTextEditor from "../../components/PortfolioTextEditor/PortfolioTextEditor";
import PostSelector from "../../components/PostSelector/PostSelector";

import { PLACE_HOLDER } from "../../constants/placeholder";
import { PAGE_URL } from "../../constants/urls";

import useMessageModal from "../../hooks/common/useMessageModal";
import useModal from "../../hooks/common/useModal";
import useAuth from "../../hooks/common/useAuth";
import useProfile from "../../hooks/service/useProfile";
import usePortfolio from "../../hooks/service/usePortfolio";
import usePortfolioIntro from "../../hooks/service/usePortfolioIntro";
import usePortfolioProjects from "../../hooks/service/usePortfolioProjects";
import usePortfolioSections from "../../hooks/service/usePortfolioSection";
import useUserFeed from "../../hooks/service/useUserFeed";

import {
  getPortfolioLocalUpdateTime,
  getPortfolioLocalUpdateTimeString,
  setPortfolioLocalUpdateTime,
} from "../../storage/storage";

import { getScrollYPosition } from "../../utils/layout";

import {
  AvatarWrapper,
  CloseButtonWrapper,
  ContactIconCSS,
  ContactWrapper,
  Container,
  DescriptionCSS,
  DetailInfo,
  FullPage,
  PaginatorWrapper,
  SectionNameCSS,
  ToggleButtonCSS,
  UserAvatarCSS,
  UserNameCSS,
} from "./MyPortfolioPage.style";

import type { PortfolioData, PortfolioProject, PortfolioSectionType, Post } from "../../@types";

const MyPortfolioPage = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [deletingSectionType, setDeletingSectionType] = useState<PortfolioSectionType>();
  const [deletingSectionName, setDeletingSectionName] = useState("");

  const { currentUsername, isLoggedIn } = useAuth();
  const { isModalShown, showModal, hideModal } = useModal(false);
  const {
    modalMessage,
    isModalShown: isMessageModalShown,
    isCancelButtonShown,
    showConfirmModal,
    hideMessageModal,
  } = useMessageModal();

  const {
    portfolio: remotePortfolio,
    isError,
    isLoading: isPortfolioLoading,
    mutateSetPortfolio,
  } = usePortfolio(currentUsername);
  const { data: profile, isLoading: isProfileLoading } = useProfile(true, currentUsername);
  const { infinitePostsData, isFetchingNextPage, handleIntersect } = useUserFeed(true, currentUsername);

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
  const { portfolioIntro, setPortfolioIntro, updateIntroName, updateIntroDescription, updateIsProfileShown } =
    usePortfolioIntro(profile?.name, profile?.description, profile?.imageUrl);

  const paginationCount = portfolioProjects.length + portfolioSections.length + 1;

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
      const localUpdateTimeString = getPortfolioLocalUpdateTimeString();
      const portfolio: PortfolioData = {
        id: remotePortfolio?.id ?? null,
        name: portfolioIntro.name,
        profileImageShown: portfolioIntro.isProfileShown,
        profileImageUrl: profile?.imageUrl ?? "",
        introduction: portfolioIntro.description,
        contacts: portfolioIntro.contacts,
        createdAt: remotePortfolio?.createdAt,
        updatedAt: localUpdateTimeString,
        projects: portfolioProjects,
        sections: portfolioSections,
      };

      await mutateSetPortfolio(portfolio);
    } catch (error) {
      console.error(error);
    }
  };

  const handleWheelEvent: React.WheelEventHandler<HTMLElement> = (event) => {
    event.preventDefault();
    console.log("wheel");
  };

  useEffect(() => {
    const localUpdateTime = getPortfolioLocalUpdateTime();

    if (remotePortfolio && remotePortfolio.updatedAt && localUpdateTime < new Date(remotePortfolio.updatedAt)) {
      const intro = {
        name: currentUsername,
        description: remotePortfolio.introduction,
        profileImageUrl: remotePortfolio.profileImageUrl,
        isProfileShown: remotePortfolio.profileImageShown,
        contacts: [...remotePortfolio.contacts],
      };

      setPortfolioIntro(intro);
      setPortfolioProjects(remotePortfolio.projects);
      setPortfolioSections(remotePortfolio.sections);
    }
  }, [remotePortfolio]);

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
    setPortfolioLocalUpdateTime(new Date());
  }, [portfolioIntro, portfolioProjects, portfolioSections]);

  if (!isLoggedIn) {
    return <Redirect to={PAGE_URL.HOME} />;
  }

  if (isError) {
    return <PageError errorMessage="포트폴리오를 불러올 수 없습니다." />;
  }

  if (isProfileLoading || isPortfolioLoading) {
    return <PageLoading />;
  }

  return (
    <>
      <ScrollActiveHeader containerRef={containerRef}>
        <PortfolioHeader
          isButtonsShown={true}
          profile={profile ?? null}
          portfolio={{
            id: null,
            intro: portfolioIntro,
            projects: portfolioProjects,
            sections: portfolioSections,
          }}
          onAddPortfolioSection={handleAddSection}
          onAddPortfolioProject={handleAddProject}
          onUploadPortfolio={handleUploadPortfolio}
        />
      </ScrollActiveHeader>
      <Container ref={containerRef} onWheel={handleWheelEvent}>
        <FullPage isVerticalCenter={true}>
          <ToggleButton
            toggleButtonText="프로필 사진 보이기"
            cssProp={ToggleButtonCSS}
            isToggled={portfolioIntro.isProfileShown}
            onToggle={() => updateIsProfileShown(!portfolioIntro.isProfileShown)}
          />
          <AvatarWrapper>
            {portfolioIntro.isProfileShown && (
              <Avatar diameter="6.5625rem" fontSize="1.5rem" imageUrl={profile?.imageUrl} cssProp={UserAvatarCSS} />
            )}
            <PortfolioTextEditor
              cssProp={UserNameCSS}
              value={portfolioIntro.name}
              onChange={handleIntroNameUpdate}
              placeholder={PLACE_HOLDER.INTRO_NAME}
              autoGrow
            />
          </AvatarWrapper>
          <PortfolioTextEditor
            cssProp={DescriptionCSS}
            value={portfolioIntro.description}
            onChange={handleIntroDescriptionUpdate}
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
        {portfolioProjects.map((portfolioProject, index) => (
          <FullPage isVerticalCenter={true} key={portfolioProject.id ?? index}>
            <PortfolioProjectSection
              isEditable={true}
              project={portfolioProject}
              setProject={handleSetProject(portfolioProject.name)}
            />
            <CloseButtonWrapper>
              <Button
                kind="roundedInline"
                padding="0.5rem"
                onClick={() => handleDeleteProjectSection(portfolioProject.name)}
              >
                <SVGIcon icon="CancelNoCircleIcon" />
              </Button>
            </CloseButtonWrapper>
          </FullPage>
        ))}
        {portfolioSections.map((portfolioSection, index) => (
          <FullPage key={portfolioSection.id ?? index}>
            <PortfolioTextEditor
              cssProp={SectionNameCSS}
              value={portfolioSection.name}
              onChange={handleSectionNameUpdate(portfolioSection.name)}
              placeholder={PLACE_HOLDER.SECTION_NAME}
              autoGrow
            />
            <PortfolioSection isEditable={true} section={portfolioSection} setSection={setPortfolioSection} />
            <CloseButtonWrapper>
              <Button
                kind="roundedInline"
                padding="0.5rem"
                onClick={() => handleDeleteCustomSection(portfolioSection.name)}
              >
                <SVGIcon icon="CancelNoCircleIcon" />
              </Button>
            </CloseButtonWrapper>
          </FullPage>
        ))}
        {isModalShown && isLoggedIn && (
          <ModalPortal onClose={hideModal} isCloseButtonShown={true}>
            <PostSelector
              infinitePostsData={infinitePostsData}
              isFetchingNextPage={isFetchingNextPage}
              onPostSelect={handlePostSelect}
              onIntersect={handleIntersect}
            />
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

export default MyPortfolioPage;
