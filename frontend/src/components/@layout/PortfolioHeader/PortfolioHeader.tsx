import { useContext } from "react";
import { useHistory } from "react-router";
import { ThemeContext } from "styled-components";
import Button from "../../@shared/Button/Button";
import DropDown, { DropDownItem } from "../../@shared/DropDown/DropDown";
import SVGIcon from "../../@shared/SVGIcon/SVGIcon";
import {
  Container,
  HeaderContentWrapper,
  HeaderButtonsWrapper,
  GoBackLinkButton,
  DropDownCSS,
} from "./PortfolioHeader.style";

export interface Props {
  onAddPortfolioSection: () => void;
  onAddPortfolioProject: () => void;
}

const PortfolioHeader = ({ onAddPortfolioSection, onAddPortfolioProject }: Props) => {
  const { color } = useContext(ThemeContext);
  const history = useHistory();

  const handleGoBack = () => {
    history.goBack();
  };

  const dropdownListItems: DropDownItem[] = [
    { text: "프로젝트 추가", onClick: onAddPortfolioProject },
    { text: "섹션 추가", onClick: onAddPortfolioSection },
  ];

  return (
    <Container>
      <HeaderContentWrapper>
        <GoBackLinkButton onClick={handleGoBack}>
          <SVGIcon icon="GoBackIcon" />
        </GoBackLinkButton>
        <HeaderButtonsWrapper>
          <DropDown items={dropdownListItems} cssProp={DropDownCSS}>
            작성란 추가
          </DropDown>
          <Button kind="roundedInline" backgroundColor={color.primaryColor} color={color.white}>
            PDF로 인쇄
          </Button>
        </HeaderButtonsWrapper>
      </HeaderContentWrapper>
    </Container>
  );
};

export default PortfolioHeader;
