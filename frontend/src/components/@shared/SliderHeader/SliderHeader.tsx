import { CSSProp } from "styled-components";
import SVGIcon from "../SVGIcon/SVGIcon";
import {
  Container,
  CloseLinkButton,
  CloseLinkButtonWrapper,
  CloseLinkText,
  GoBackLinkButton,
} from "./SliderHeader.style";

export interface Props {
  onSlideDown?: () => void;
  cssProp?: CSSProp;
}

const SliderHeader = ({ onSlideDown, cssProp }: Props) => {
  return (
    <Container cssProp={cssProp}>
      <CloseLinkButtonWrapper onClick={onSlideDown}>
        <CloseLinkText>내리기</CloseLinkText>
        <CloseLinkButton>
          <SVGIcon icon="GoDownIcon" />
        </CloseLinkButton>
      </CloseLinkButtonWrapper>
    </Container>
  );
};

export default SliderHeader;
