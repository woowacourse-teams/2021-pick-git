import { useRef } from "react";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Avatar from "../../components/@shared/Avatar/Avatar";
import useProfile from "../../hooks/service/useProfile";
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
  UserAvatarCSS,
  ContactIconCSS,
} from "./PortfolioPage.style";
import DotPaginator from "../../components/@shared/DotPaginator/DotPaginator";
import ScrollActiveHeader from "../../components/@layout/ScrollActiveHeader/ScrollActiveHeader";
import PortfolioHeader from "../../components/@layout/PortfolioHeader/PortfolioHeader";
import { getScrollYPosition } from "../../utils/layout";
import PortfolioProjectSection from "../../components/PortfolioProjectSection/PortfolioProjectSection";
import PortfolioSection from "../../components/PortfolioSection/PortfolioSection";
import SVGIcon from "../../components/@shared/SVGIcon/SVGIcon";
import PortfolioTextEditor from "../../components/PortfolioTextEditor/PortfolioTextEditor";
import usePortfolio from "../../hooks/service/usePortfolio";
import PageError from "../../components/@shared/PageError/PageError";

const PortfolioPage = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const username = new URLSearchParams(location.search).get("username") ?? "";
  const { portfolio: remotePortfolio, isError, isLoading: isPortfolioLoading, error } = usePortfolio(username);
  const { data: profile, isLoading: isProfileLoading } = useProfile(false, username);

  const handlePaginate = (index: number) => {
    if (!containerRef.current) {
      return;
    }

    containerRef.current.scrollTo({
      behavior: "smooth",
      top: getScrollYPosition(containerRef.current.children[index], containerRef.current),
    });
  };

  if (isProfileLoading || isPortfolioLoading) {
    return <PageLoading />;
  }

  if (!remotePortfolio || isError) {
    if (error?.response?.status === 400) {
      return (
        <>
          <PortfolioHeader isButtonsShown={false} />
          <PageError errorMessage="아직 포트폴리오가 만들어지지 않았습니다" />
        </>
      );
    }

    return <div>포트폴리오 정보를 불러오는데 실패했습니다</div>;
  }

  const paginationCount = remotePortfolio.projects.length + remotePortfolio.sections.length + 1;

  return (
    <>
      <ScrollActiveHeader containerRef={containerRef}>
        <PortfolioHeader isButtonsShown={false} />
      </ScrollActiveHeader>
      <Container ref={containerRef}>
        <FullPage isVerticalCenter={true}>
          <AvatarWrapper>
            {remotePortfolio.profileImageShown && (
              <Avatar
                diameter="6.5625rem"
                fontSize="1.5rem"
                imageUrl={remotePortfolio.profileImageUrl}
                cssProp={UserAvatarCSS}
              />
            )}
            <PortfolioTextEditor cssProp={UserNameCSS} value={remotePortfolio.name} disabled autoGrow={false} />
          </AvatarWrapper>
          <PortfolioTextEditor
            cssProp={DescriptionCSS}
            value={remotePortfolio.introduction}
            disabled
            autoGrow={false}
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
        {remotePortfolio.projects.map((portfolioProject) => (
          <FullPage isVerticalCenter={true} key={portfolioProject.id}>
            <PortfolioProjectSection isEditable={false} project={portfolioProject} />
          </FullPage>
        ))}
        {remotePortfolio.sections.map((portfolioSection) => (
          <FullPage key={portfolioSection.id}>
            <PortfolioTextEditor cssProp={SectionNameCSS} value={portfolioSection.name} autoGrow={false} disabled />
            <PortfolioSection isEditable={false} section={portfolioSection} />
          </FullPage>
        ))}
      </Container>
      <PaginatorWrapper>
        <DotPaginator paginationCount={paginationCount} paginate={handlePaginate} />
      </PaginatorWrapper>
    </>
  );
};

export default PortfolioPage;
