import { useEffect, useRef, useState } from "react";
import { Redirect } from "react-router-dom";

import AlertPortal from "../../components/@layout/AlertPortal/AlertPortal";
import ConfirmPortal from "../../components/@layout/ConfirmPortal/ConfirmPortal";
import ModalPortal from "../../components/@layout/Modal/ModalPortal";
import BottomSliderPortal from "../../components/@layout/BottomSliderPortal/BottomSliderPortal";
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
import PortfolioContactForm from "../../components/PortfolioContactForm/PortfolioContactForm";
import PortfolioTextEditor from "../../components/PortfolioTextEditor/PortfolioTextEditor";
import PostSelector from "../../components/PostSelector/PostSelector";

import { PLACE_HOLDER } from "../../constants/placeholder";
import { PAGE_URL } from "../../constants/urls";

import useModal from "../../hooks/common/useModal";
import useAuth from "../../hooks/common/useAuth";
import useBottomSlider from "../../hooks/common/useBottomSlider";
import useProfile from "../../hooks/service/useProfile";
import usePortfolio from "../../hooks/service/usePortfolio";
import usePortfolioIntro from "../../hooks/service/usePortfolioIntro";
import usePortfolioProjects from "../../hooks/service/usePortfolioProjects";
import usePortfolioSections from "../../hooks/service/usePortfolioSection";
import useUserFeed from "../../hooks/service/useUserFeed";

import { getPortfolioLocalUpdateTime, getPortfolioLocalUpdateTimeString } from "../../storage/storage";

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
  PostSelectorModalCSS,
  SectionNameCSS,
  ToggleButtonCSS,
  UserAvatarCSS,
  UserNameCSS,
} from "./PortfolioPage.style";

import type { PortfolioData, PortfolioProject, PortfolioSectionType, Post } from "../../@types";
import useScrollPagination from "../../hooks/common/useScrollPagination";
import usePortfolioContacts from "../../hooks/service/usePortfolioContacts";
import { CONTACT_ICON } from "../../constants/portfolio";
import useSnackbar from "../../hooks/common/useSnackbar";

const MyPortfolioPage = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [deletingSectionType, setDeletingSectionType] = useState<PortfolioSectionType>();
  const [deletingSectionName, setDeletingSectionName] = useState("");

  const { currentUsername, isLoggedIn } = useAuth();
  const {
    isModalShown: isProjectAddModalShown,
    showModal: showProjectAddModal,
    hideModal: hideProjectAddModal,
  } = useModal(false);
  const {
    modalMessage: alertMessage,
    isModalShown: isAlertShown,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();
  const {
    modalMessage: confirmMessage,
    isModalShown: isConfirmShown,
    showModal: showConfirm,
    hideModal: hideConfirm,
  } = useModal();
  const { isBottomSliderShown, showBottomSlider, setSlideEventHandler, removeSlideEventHandler, hideBottomSlider } =
    useBottomSlider();
  const { pushSnackbarMessage } = useSnackbar();

  const {
    portfolio: remotePortfolio,
    isLoading: isPortfolioLoading,
    isError,
    isFetching,
    mutateSetPortfolio,
  } = usePortfolio(currentUsername, true);
  const { data: profile, isLoading: isProfileLoading } = useProfile(true, currentUsername);
  const { infinitePostsData, isFetchingNextPage, handleIntersect } = useUserFeed(true, currentUsername);

  const {
    portfolioSections,
    addBlankPortfolioSection,
    deletePortfolioSection,
    updatePortfolioSectionName,
    setPortfolioSection,
    setPortfolioSections,
  } = usePortfolioSections(currentUsername);
  const {
    portfolioProjects,
    addPortfolioProject,
    deletePortfolioProject,
    updatePortfolioProject,
    setPortfolioProjects,
  } = usePortfolioProjects(currentUsername);
  const { portfolioContacts, setPortfolioContact, setPortfolioContacts } = usePortfolioContacts(currentUsername);
  const { portfolioIntro, setPortfolioIntro, updateIntroName, updateIntroDescription, updateIsProfileShown } =
    usePortfolioIntro(currentUsername, profile?.name, profile?.description, profile?.imageUrl);

  const paginationCount = portfolioProjects.length + portfolioSections.length + 1;
  const { paginate } = useScrollPagination(containerRef, paginationCount);

  const handleSetProject = (prevProjectName: string) => (newProject: PortfolioProject) => {
    updatePortfolioProject(prevProjectName, newProject);
  };

  const handleAddSection = () => {
    addBlankPortfolioSection();
  };

  const handleAddProject = () => {
    showProjectAddModal();
  };

  const handleSectionNameUpdate = (prevSectionName: string) => (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    updatePortfolioSectionName(prevSectionName, event.currentTarget.value);
  };

  const handleIntroNameUpdate: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateIntroName(event.currentTarget.value);
  };

  const handleIntroDescriptionUpdate: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    if (event.currentTarget.value.length > 200) {
      showAlert("200자 이상의 자기소개를 작성하실 수 없습니다.");
      return;
    }

    updateIntroDescription(event.currentTarget.value);
  };

  const handlePostSelect = (post: Post) => {
    const [firstImageUrl] = post.imageUrls;

    addPortfolioProject({
      id: null,
      content: post.content,
      imageUrl: firstImageUrl,
      name: `프로젝트 ${portfolioProjects.length + 1}`,
      startDate: "",
      endDate: "",
      tags: post.tags,
      type: "team",
    });

    paginate(portfolioProjects.length + 1);

    hideProjectAddModal();
  };

  const handleDeleteProjectSection = (sectionName: string) => {
    showConfirm("정말 삭제하시겠습니까?");
    setDeletingSectionType("project");
    setDeletingSectionName(sectionName);
  };

  const handleDeleteCustomSection = (sectionName: string) => {
    showConfirm("정말 삭제하시겠습니까?");
    setDeletingSectionType("custom");
    setDeletingSectionName(sectionName);
  };

  const handleDeleteSectionConfirm = () => {
    if (deletingSectionType === "project") {
      deletePortfolioProject(deletingSectionName);
      hideConfirm();
      return;
    }

    deletePortfolioSection(deletingSectionName);
    hideConfirm();
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
        contacts: portfolioContacts,
        createdAt: remotePortfolio?.createdAt,
        updatedAt: localUpdateTimeString,
        projects: portfolioProjects,
        sections: portfolioSections,
      };

      await mutateSetPortfolio(portfolio);

      pushSnackbarMessage("포트폴리오 업로드 성공");
    } catch (error) {
      console.error(error);
      showAlert("포트폴리오를 서버에 저장하는데 실패했습니다.");
    }
  };

  const handleSlideDown = () => {
    hideBottomSlider();
  };

  const handleSetContacts = () => {
    showBottomSlider();
  };

  useEffect(() => {
    if (!remotePortfolio || !remotePortfolio.updatedAt) {
      return;
    }

    const syncRemoteWithLocal = () => {
      const intro = {
        name: currentUsername,
        description: remotePortfolio.introduction,
        profileImageUrl: remotePortfolio.profileImageUrl,
        isProfileShown: remotePortfolio.profileImageShown,
        contacts: [...remotePortfolio.contacts],
      };

      setPortfolioIntro(intro, false);
      setPortfolioProjects(remotePortfolio.projects, false);
      setPortfolioSections(remotePortfolio.sections, false);
      setPortfolioContacts(remotePortfolio.contacts, false);
    };

    const localUpdateTime = getPortfolioLocalUpdateTime();

    if (!localUpdateTime) {
      syncRemoteWithLocal();
      return;
    }

    if (localUpdateTime < new Date(remotePortfolio.updatedAt)) {
      syncRemoteWithLocal();
    }
  }, [remotePortfolio]);

  useEffect(() => {
    if (profile && portfolioIntro.name === "" && portfolioIntro.description === "") {
      setPortfolioIntro(
        {
          ...portfolioIntro,
          name: profile.name,
          description: profile.description,
        },
        false
      );
    }
  }, [profile]);

  useEffect(() => {
    paginate(paginationCount - 1);
  }, [portfolioSections.length]);

  useEffect(() => {
    setSlideEventHandler();

    return () => removeSlideEventHandler();
  }, []);

  if (!isLoggedIn) {
    return <Redirect to={PAGE_URL.HOME} />;
  }

  if (isError) {
    return <PageError errorMessage="포트폴리오를 불러올 수 없습니다." />;
  }

  if (isProfileLoading || isPortfolioLoading || isFetching) {
    return <PageLoading />;
  }

  return (
    <>
      <ScrollActiveHeader containerRef={containerRef}>
        <PortfolioHeader
          isButtonsShown={true}
          onSetPortfolioContacts={handleSetContacts}
          onAddPortfolioSection={handleAddSection}
          onAddPortfolioProject={handleAddProject}
          onUploadPortfolio={handleUploadPortfolio}
        />
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
            {portfolioContacts
              .filter((portfolioContact) => portfolioContact.value !== "")
              .map((portfolioContact) => (
                <DetailInfo>
                  <SVGIcon cssProp={ContactIconCSS} icon={CONTACT_ICON[portfolioContact.category]} />
                  {portfolioContact.value}
                </DetailInfo>
              ))}
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
        {isProjectAddModalShown && isLoggedIn && (
          <ModalPortal onClose={hideProjectAddModal} isCloseButtonShown={true}>
            <PostSelector
              infinitePostsData={infinitePostsData}
              isFetchingNextPage={isFetchingNextPage}
              onPostSelect={handlePostSelect}
              onIntersect={handleIntersect}
            />
          </ModalPortal>
        )}
        {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
        {isConfirmShown && (
          <ConfirmPortal heading={confirmMessage} onConfirm={handleDeleteSectionConfirm} onCancel={hideConfirm} />
        )}
        <BottomSliderPortal onSlideDown={handleSlideDown} isSliderShown={isBottomSliderShown}>
          <PortfolioContactForm
            portfolioContacts={portfolioContacts}
            setPortfolioContact={setPortfolioContact}
            onEditComplete={handleSlideDown}
          />
        </BottomSliderPortal>
      </Container>
      {/* <PaginatorWrapper>
        <DotPaginator activePageIndex={activePageIndex} paginationCount={paginationCount} onPaginate={paginate} />
      </PaginatorWrapper> */}
    </>
  );
};

export default MyPortfolioPage;
