import styled, { css, CSSProp } from "styled-components";
import { Z_INDEX } from "../../../constants/layout";
import { fadeIn } from "../../@styled/keyframes";
import { setTabletAboveMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: ${Z_INDEX.MIDDLE};

    display: flex;
    justify-content: center;
    align-items: center;
    animation: ${fadeIn} 0.5s forwards;

    ${cssProp}
  `
);

export const ModalContent = styled.div`
  position: relative;
  border-radius: 4px;
  background-color: ${({ theme }) => theme.color.white};
`;

export const CloseButtonWrapper = styled.div`
  position: absolute;
  z-index: ${Z_INDEX.HIGH};
  top: -1rem;
  right: -1rem;
  display: flex;
  justify-content: flex-end;

  > button:active {
    transform: scale(0.98);
  }

  ${setTabletAboveMediaQuery`
    top: -1rem;
    right: -1rem;
  `}
`;
