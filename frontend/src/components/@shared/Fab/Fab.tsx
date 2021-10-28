import { useState } from "react";
import { CSSProp } from "styled-components";
import SVGIcon, { IconType } from "../SVGIcon/SVGIcon";
import { ChildFab, FabText, StyledFab, ChildFabWrapper, BackDrop } from "./Fab.style";

import type { ChildFabItem } from "../../../@types";

export interface Props {
  childFabs?: ChildFabItem[];
  color?: string;
  backgroundColor?: string;
  fabCssProp?: CSSProp;
  childFabCssProp?: CSSProp;
  icon: IconType;
}

const Fab = ({ icon, childFabs, color, backgroundColor, fabCssProp, childFabCssProp }: Props) => {
  const [isChildFabsShown, setIsChildFabsShown] = useState(false);

  const handleShowChildFabs = () => {
    setIsChildFabsShown(!isChildFabsShown);
  };

  const handleChildFabsClick = (onClick: () => void) => () => {
    setIsChildFabsShown(false);
    onClick();
  };

  const childFabComponents = childFabs
    ? childFabs.map((childFab, index) => (
        <ChildFabWrapper key={index} index={index} isShown={isChildFabsShown}>
          {childFab.text && <FabText isShown={isChildFabsShown}>{childFab.text}</FabText>}
          <ChildFab
            backgroundColor={childFab.backgroundColor}
            isShown={isChildFabsShown}
            onClick={handleChildFabsClick(childFab.onClick)}
          >
            <SVGIcon icon={childFab.icon} cssProp={childFabCssProp} />
          </ChildFab>
        </ChildFabWrapper>
      ))
    : [];

  return (
    <>
      <BackDrop isShown={isChildFabsShown} onClick={handleShowChildFabs} />
      <StyledFab onClick={handleShowChildFabs} color={color} backgroundColor={backgroundColor}>
        <SVGIcon icon={icon} cssProp={fabCssProp} />
      </StyledFab>
      {childFabComponents}
    </>
  );
};

export default Fab;
