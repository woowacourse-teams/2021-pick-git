import { useState } from "react";
import { CSSProp } from "styled-components";
import SVGIcon from "../SVGIcon/SVGIcon";
import {
  Container,
  DropDownList,
  DropDownListItem,
  DropDownIconCSS,
  ToggleLinkButton,
  ToggleLinkButtonText,
} from "./DropDown.style";

export interface DropDownItem {
  text: string;
  onClick?: () => void;
}

export interface Props {
  items: DropDownItem[];
  children: React.ReactNode;
  cssProp?: CSSProp;
}

const DropDown = ({ cssProp, items, children }: Props) => {
  const [isElementShown, setIsElementShown] = useState(false);

  const handleToggleElement = () => {
    setIsElementShown(!isElementShown);
  };

  const handleDropDownListItemClick = (item: DropDownItem) => {
    setIsElementShown(false);
    item.onClick && item.onClick();
  };

  return (
    <Container cssProp={cssProp}>
      <ToggleLinkButton onClick={handleToggleElement}>
        <ToggleLinkButtonText>{children}</ToggleLinkButtonText>{" "}
        <SVGIcon icon="ArrowDownIcon" cssProp={DropDownIconCSS} />
      </ToggleLinkButton>
      <DropDownList isShown={isElementShown}>
        {items.map((item) => (
          <DropDownListItem key={item.text} onClick={() => handleDropDownListItemClick(item)}>
            {item.text}
          </DropDownListItem>
        ))}
      </DropDownList>
    </Container>
  );
};

export default DropDown;
