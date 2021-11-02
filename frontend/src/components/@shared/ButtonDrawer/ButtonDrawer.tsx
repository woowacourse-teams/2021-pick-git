import { useState } from "react";
import { CSSProp } from "styled-components";
import { CircleButtonItem } from "../../../@types";
import SVGIcon, { IconType } from "../SVGIcon/SVGIcon";
import { Container, CircleButton, IconWrapper } from "./ButtonDrawer.style";

export interface Props {
  icon?: IconType;
  circleButtons: CircleButtonItem[];
  containerCssProp?: CSSProp;
}

const ButtonDrawer = ({ icon = "VerticalDotsIcon", circleButtons, containerCssProp }: Props) => {
  const [isButtonsShown, setIsButtonsShown] = useState(false);

  const circleButtonItems = circleButtons.map((circleButton, index) => (
    <CircleButton
      key={index}
      isShown={isButtonsShown}
      index={index}
      buttonsCount={circleButtons.length}
      onClick={circleButton.onClick}
      backgroundColor={circleButton.backgroundColor}
    >
      <SVGIcon icon={circleButton.icon} />
    </CircleButton>
  ));

  const handleVerticalDotsClick = () => {
    setIsButtonsShown(!isButtonsShown);
  };

  return (
    <Container cssProp={containerCssProp}>
      <IconWrapper onClick={handleVerticalDotsClick}>
        <SVGIcon icon={icon} />
      </IconWrapper>
      {circleButtonItems}
    </Container>
  );
};

export default ButtonDrawer;
