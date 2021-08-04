import { createPortal } from "react-dom";
import { Container } from "./BottomSliderPortal.styles";

export interface Props {
  children: React.ReactNode;
  isSliderShown: boolean;
}

export const BottomSlider = ({ isSliderShown, children }: Props) => {
  return <Container isSliderShown={isSliderShown}>{children}</Container>;
};

const BottomSliderPortal = ({ ...props }: Props) => {
  const $BottomSliderWrapper = document.getElementById("bottom-slider");
  if (!$BottomSliderWrapper) throw Error("cannot find bottom-slider wrapper");

  return createPortal(<BottomSlider {...props} />, $BottomSliderWrapper);
};

export default BottomSliderPortal;
