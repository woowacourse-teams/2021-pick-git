import { useState } from "react";
import { VerticalDotsIcon } from "../../../assets/icons";
import { Container, CircleButton, VerticalDotsWrapper } from "./ButtonDrawer.style";

type CircleButton = {
  node: React.ReactNode;
  onClick: () => void;
};

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  circleButtons: CircleButton[];
}

const ButtonDrawer = ({ circleButtons }: Props) => {
  const [isButtonsShown, setIsButtonsShown] = useState(false);

  const circleButtonItems = circleButtons.map((circleButton, index) => (
    <CircleButton
      key={index}
      isShown={isButtonsShown}
      index={index}
      buttonsCount={circleButtons.length}
      onClick={circleButton.onClick}
    >
      {circleButton.node}
    </CircleButton>
  ));

  const handleVerticalDotsClick = () => {
    setIsButtonsShown(!isButtonsShown);
  };

  return (
    <Container>
      <VerticalDotsWrapper onClick={handleVerticalDotsClick}>
        <VerticalDotsIcon />
      </VerticalDotsWrapper>
      {circleButtonItems}
    </Container>
  );
};

export default ButtonDrawer;
