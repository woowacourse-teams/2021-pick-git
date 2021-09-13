import { css, CSSProp } from "styled-components";

const device = {
  mobile: "425px",
  tablet: "768px",
  laptop: "1280px",
};

export const setMobileMediaQuery = (literals: TemplateStringsArray, ...args: string[]): CSSProp => {
  return css`
    @media only screen and (max-width: ${device.mobile}) {
      ${css(literals, ...args)}
    }
  `;
};

export const setTabletMediaQuery = (literals: TemplateStringsArray, ...args: string[]): CSSProp => {
  return css`
    @media only screen and (min-width: ${device.mobile}) and (max-width: ${device.tablet}) {
      ${css(literals, ...args)}
    }
  `;
};

export const setTabletAboveMediaQuery = (literals: TemplateStringsArray, ...args: string[]): CSSProp => {
  return css`
    @media only screen and (min-width: ${device.mobile}) {
      ${css(literals, ...args)}
    }
  `;
};

export const setLaptopMediaQuery = (literals: TemplateStringsArray, ...args: string[]): CSSProp => {
  return css`
    @media only screen and (min-width: ${device.tablet}) and (max-width: ${device.laptop}) {
      ${css(literals, ...args)}
    }
  `;
};

export const setLaptopAboveMediaQuery = (literals: TemplateStringsArray, ...args: string[]): CSSProp => {
  return css`
    @media only screen and (min-width: ${device.tablet}) {
      ${css(literals, ...args)}
    }
  `;
};

export const setDesktopMediaQuery = (literals: TemplateStringsArray, ...args: string[]): CSSProp => {
  return css`
    @media only screen and (min-width: ${device.laptop}) {
      ${css(literals, ...args)}
    }
  `;
};
