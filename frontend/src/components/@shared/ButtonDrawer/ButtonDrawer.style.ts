import styled, { css, CSSProp } from "styled-components";
import { Z_INDEX } from "../../../constants/layout";

export const Container = styled.span<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    position: relative;
    display: inline-flex;
    justify-content: center;
    align-items: center;

    ${cssProp}
  `
);

export const IconWrapper = styled.div`
  width: 100%;
  height: 100%;
  cursor: pointer;
`;

export const CircleButton = styled.div<{
  isShown: boolean;
  index: number;
  buttonsCount: number;
  backgroundColor?: string;
}>(
  ({ theme, isShown, index, buttonsCount, backgroundColor }) => css`
    position: absolute;
    top: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    border-radius: 50%;
    overflow: hidden;
    width: 2.2rem;
    height: 2.2rem;
    background-color: ${backgroundColor ?? theme.color.white};
    box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
    transition: box-shadow 0.5s, opacity 0.5s, transform 0.5s;
    visibility: hidden;
    opacity: 0;
    z-index: ${Z_INDEX.LOW};

    :hover {
      box-shadow: 2px 6px 12px rgba(0, 0, 0, 0.2);
    }

    visibility: ${isShown ? "visible" : "hidden"};
    opacity: ${isShown ? "1" : "0"};
    transform: ${isShown
      ? `translate(${(-4 * index) / buttonsCount - 0.3}rem, ${(-4 * index) / buttonsCount + 1.5}rem)`
      : ""};
  `
);
