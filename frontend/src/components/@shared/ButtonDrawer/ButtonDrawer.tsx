import { useState } from "react";
import { CircleButtonItem } from "../../../@types";
import SVGIcon, { IconType } from "../SVGIcon/SVGIcon";
import { Container, CircleButton, IconWrapper } from "./ButtonDrawer.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  icon?: IconType;
  circleButtons: CircleButtonItem[];
}

const ButtonDrawer = ({ icon = "VerticalDotsIcon", circleButtons }: Props) => {
  const [isButtonsShown, setIsButtonsShown] = useState(false);

  const circleButtonItems = circleButtons.map((circleButton, index) => (
    <CircleButton
      key={index}
      isShown={isButtonsShown}
      index={index}
      buttonsCount={circleButtons.length}
      onClick={circleButton.onClick}
    >
      <SVGIcon icon={circleButton.icon} />
    </CircleButton>
  ));

  const handleVerticalDotsClick = () => {
    setIsButtonsShown(!isButtonsShown);
  };

  return (
    <Container>
      <IconWrapper onClick={handleVerticalDotsClick}>
        <SVGIcon icon={icon} />
      </IconWrapper>
      {circleButtonItems}
    </Container>
  );
};

export default ButtonDrawer;
