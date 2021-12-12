import { useRef } from "react";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import PortfolioHeader from "../../components/@layout/PortfolioHeader/PortfolioHeader";
import ScrollActiveHeader from "../../components/@layout/ScrollActiveHeader/ScrollActiveHeader";
import Avatar from "../../components/@shared/Avatar/Avatar";
import PageError from "../../components/@shared/PageError/PageError";
import SVGIcon from "../../components/@shared/SVGIcon/SVGIcon";
import PortfolioProjectSection from "../../components/PortfolioProjectSection/PortfolioProjectSection";
import PortfolioSection from "../../components/PortfolioSection/PortfolioSection";
import PortfolioTextEditor from "../../components/PortfolioTextEditor/PortfolioTextEditor";
import { CONTACT_ICON } from "../../constants/portfolio";

import usePortfolio from "../../hooks/service/usePortfolio";
import useProfile from "../../hooks/service/useProfile";
import { customError } from "../../utils/error";

import {
  AvatarWrapper,
  ContactIconCSS,
  ContactWrapper,
  Container,
  DescriptionCSS,
  DetailInfo,
  FullPage,
  SectionNameCSS,
  UserAvatarCSS,
  UserNameCSS,
} from "./PortfolioPage.style";

const PortfolioPage = () => {
  const username = new URLSearchParams(location.search).get("username") ?? "";
  const containerRef = useRef<HTMLDivElement>(null);

  const {
    portfolio: remotePortfolio,
    isLoading: isPortfolioLoading,
    isError,
    isFetching,
  } = usePortfolio(username, false);

  if (isPortfolioLoading || isFetching) {
    return <PageLoading />;
  }

  if (isError) {
    return <PageError errorMessage="포트폴리오 정보를 불러오는데 실패했습니다" />;
  }

  if (!remotePortfolio) {
    return (
      <>
        <PortfolioHeader username={username} />
        <PageError errorMessage="아직 포트폴리오가 만들어지지 않았습니다" />
      </>
    );
  }

  return (
    <>
      <ScrollActiveHeader containerRef={containerRef}>
        <PortfolioHeader portfolio={remotePortfolio} username={username} />
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
            <PortfolioTextEditor cssProp={UserNameCSS} value={remotePortfolio.name} disabled autoGrow />
          </AvatarWrapper>
          <PortfolioTextEditor
            cssProp={DescriptionCSS}
            value={remotePortfolio.introduction}
            disabled
            autoGrow={false}
          />
          <ContactWrapper>
            {remotePortfolio.contacts
              .filter((portfolioContact) => portfolioContact.value !== "")
              .map((portfolioContact) => (
                <DetailInfo>
                  <SVGIcon cssProp={ContactIconCSS} icon={CONTACT_ICON[portfolioContact.category]} />
                  {portfolioContact.value}
                </DetailInfo>
              ))}
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
    </>
  );
};

export default PortfolioPage;
