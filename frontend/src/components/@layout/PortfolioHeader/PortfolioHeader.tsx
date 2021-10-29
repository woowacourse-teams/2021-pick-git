import { useHistory } from "react-router";
// import { PDFDownloadLink } from "@react-pdf/renderer";

import SVGIcon from "../../@shared/SVGIcon/SVGIcon";
import {
  Container,
  HeaderContentWrapper,
  HeaderButtonsWrapper,
  GoBackLinkButton,
  KaKaoShareButton,
  LinkShareButton,
} from "./PortfolioHeader.style";
// import PortfolioDocument from "../../PortfolioDocument/PortfolioDocument";
import { Portfolio, ProfileData, PortfolioData } from "../../../@types";
import ShareLink from "../../@shared/ShareLink/ShareLink";
import useSnackbar from "../../../hooks/common/useSnackbar";
import { PAGE_URL } from "../../../constants/urls";

export interface Props {
  isButtonsShown?: boolean;
  profile?: ProfileData | null;
  portfolio?: Portfolio | PortfolioData;
  username: string;
}

const PortfolioHeader = ({ isButtonsShown = true, portfolio, username }: Props) => {
  const history = useHistory();
  const { pushSnackbarMessage } = useSnackbar();

  const handleGoBack = () => {
    if (history.length < 3) {
      history.push(PAGE_URL.HOME);
      return;
    }

    history.goBack();
  };

  const handleShareLinkCopy = () => {
    navigator.clipboard.writeText(PAGE_URL.USER_PORTFOLIO_SHARE(username));
    pushSnackbarMessage(`포트폴리오 링크가 복사되었습니다`);
  };

  return (
    <Container>
      <HeaderContentWrapper>
        <GoBackLinkButton onClick={handleGoBack}>
          <SVGIcon icon="GoBackIcon" />
        </GoBackLinkButton>
        {isButtonsShown && (
          <HeaderButtonsWrapper>
            {portfolio && (
              <>
                <LinkShareButton onClick={handleShareLinkCopy}>
                  <SVGIcon icon="CopyIcon" />
                </LinkShareButton>
                <ShareLink target={portfolio} username={username}>
                  <KaKaoShareButton>
                    <SVGIcon icon="KakaoIcon" />
                  </KaKaoShareButton>
                </ShareLink>
              </>
            )}
          </HeaderButtonsWrapper>
        )}
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
