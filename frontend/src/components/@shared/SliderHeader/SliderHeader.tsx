import { CSSProp } from "styled-components";
import SVGIcon from "../SVGIcon/SVGIcon";
import { Container, CloseLinkButton, CloseLinkButtonWrapper } from "./SliderHeader.style";

export interface Props {
  onSlideDown?: () => void;
  cssProp?: CSSProp;
}

const SliderHeader = ({ onSlideDown, cssProp }: Props) => {
  return (
    <Container cssProp={cssProp}>
      <CloseLinkButtonWrapper onClick={onSlideDown}>
        <CloseLinkButton>
          <SVGIcon icon="GoDownIcon" />
        </CloseLinkButton>
      </CloseLinkButtonWrapper>
    </Container>
  );
};

export default SliderHeader;
