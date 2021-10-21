import { createPortal } from "react-dom";
import SliderHeader from "../../@shared/SliderHeader/SliderHeader";

import { Container } from "./BottomSliderPortal.styles";

export interface Props {
  children: React.ReactNode;
  isSliderShown: boolean;
  onSlideDown: () => void;
}

export const BottomSlider = ({ isSliderShown, children, onSlideDown }: Props) => {
  return (
    <Container isSliderShown={isSliderShown}>
      <SliderHeader onSlideDown={onSlideDown} />
      {children}
    </Container>
  );
};

const BottomSliderPortal = ({ ...props }: Props) => {
  const $BottomSliderWrapper = document.getElementById("bottom-slider");
  if (!$BottomSliderWrapper) throw Error("cannot find bottom-slider wrapper");

  return createPortal(<BottomSlider {...props} />, $BottomSliderWrapper);
};

export default BottomSliderPortal;
