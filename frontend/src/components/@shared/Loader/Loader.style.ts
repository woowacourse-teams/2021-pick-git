import styled, { css, CSSProp } from "styled-components";
import { bounceAnimation, spinAnimation } from "../../@styled/keyframes";

export const LoadingDots = styled.div<{ cssProp?: CSSProp; isShown: boolean }>(
  ({ cssProp, isShown }) => `
    ${isShown ? "display: block" : "display: none"}
    ${cssProp}
  `
);

const LoadingDot = styled.div<{ size: string; loaderColor?: string }>(
  ({ theme, size, loaderColor }) => css`
    width: ${size};
    height: ${size};
    background-color: ${loaderColor ?? theme.color.tagItemColor};
    border-radius: 50%;
    display: inline-block;
    animation: 1.5s ${bounceAnimation} infinite ease-in-out both;
  `
);

export const FirstLoadingDot = styled(LoadingDot)`
  margin-right: 0.3rem;
`;

export const SecondLoadingDot = styled(LoadingDot)`
  margin-right: 0.3rem;
  animation-delay: 0.15s;
`;

export const ThirdLoadingDot = styled(LoadingDot)`
  animation-delay: 0.3s;
`;

export const Spinner = styled.div<{ size: string; isShown: boolean; loaderColor?: string; cssProp?: CSSProp }>(
  ({ theme, size, isShown, loaderColor, cssProp }) => css`
    ${isShown ? "display: inline-block;" : "display: none;"}
    width: ${size};
    height: ${size};
    border: 3px solid ${loaderColor ? "transparent" : theme.color.tagItemColor};
    border-top-color: ${loaderColor ?? theme.color.primaryColor};
    border-radius: 50%;
    animation: 1s ${spinAnimation} infinite ease-in-out;

    ${cssProp}
  `
);
