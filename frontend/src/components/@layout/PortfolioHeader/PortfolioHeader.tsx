import { useContext } from "react";
import { useHistory } from "react-router";
import { ThemeContext } from "styled-components";
// import { PDFDownloadLink } from "@react-pdf/renderer";

import Button from "../../@shared/Button/Button";
import SVGIcon from "../../@shared/SVGIcon/SVGIcon";
import { Container, HeaderContentWrapper, HeaderButtonsWrapper, GoBackLinkButton } from "./PortfolioHeader.style";
// import PortfolioDocument from "../../PortfolioDocument/PortfolioDocument";
import { Portfolio, ProfileData } from "../../../@types";

export interface Props {
  isButtonsShown?: boolean;
  profile?: ProfileData | null;
  portfolio?: Portfolio;
}

const PortfolioHeader = ({ isButtonsShown = true }: Props) => {
  const { color } = useContext(ThemeContext);
  const history = useHistory();

  const handleGoBack = () => {
    history.goBack();
  };

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
