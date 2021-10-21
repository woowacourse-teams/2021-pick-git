import styled, { css, CSSProp } from "styled-components";
import { LAYOUT } from "../../../constants/layout";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 100%;
    min-height: ${LAYOUT.HEADER_HEIGHT};
    padding: 1.0625rem 1.375rem;

    ${cssProp}
  `
);

export const CloseLinkButtonWrapper = styled.div`
  display: flex;
  cursor: pointer;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const CloseLinkButton = styled.a``;
