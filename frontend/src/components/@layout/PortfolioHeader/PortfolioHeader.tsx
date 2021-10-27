import { useContext } from "react";
import { useHistory } from "react-router";
import { ThemeContext } from "styled-components";
// import { PDFDownloadLink } from "@react-pdf/renderer";

import Button from "../../@shared/Button/Button";
import SVGIcon from "../../@shared/SVGIcon/SVGIcon";
import { Container, HeaderContentWrapper, HeaderButtonsWrapper, GoBackLinkButton } from "./PortfolioHeader.style";
// import PortfolioDocument from "../../PortfolioDocument/PortfolioDocument";
import { Portfolio, ProfileData, PortfolioData } from "../../../@types";
// import ShareLink from "../../@shared/ShareLink/ShareLink";
// import useAuth from "../../../hooks/common/useAuth";

export interface Props {
  isButtonsShown?: boolean;
  profile?: ProfileData | null;
  portfolio: Portfolio | PortfolioData | null;
  username: string;
}

const PortfolioHeader = ({ isButtonsShown = true, portfolio, username }: Props) => {
  const { color } = useContext(ThemeContext);
  const history = useHistory();

  const handleGoBack = () => {
    history.goBack();
  };

  console.log("portfolio", portfolio);

  return (
    <Container>
      <HeaderContentWrapper>
        <GoBackLinkButton onClick={handleGoBack}>
          <SVGIcon icon="GoBackIcon" />
        </GoBackLinkButton>
        {/* {isButtonsShown && (
          <HeaderButtonsWrapper>
            <Button kind="roundedInline" backgroundColor={color.primaryColor} color={color.white}>
              <PDFDownloadLink
                document={<PortfolioDocument profile={profile} portfolio={portfolio} />}
                fileName="포트폴리오.pdf"
                style={{ color: "inherit", fontSize: "inherit" }}
              >
                PDF로 인쇄
              </PDFDownloadLink>
            </Button>
          </HeaderButtonsWrapper>
        )} */}
      </HeaderContentWrapper>
    </Container>
  );
};

export default PortfolioHeader;
