import styled, { css, CSSProp } from "styled-components";

export interface StyleProps extends RoundedContainerProps, BottomBorderContainerProps, InputProps {}

interface RoundedContainerProps {
  backgroundColor?: string;
  cssProp?: CSSProp;
}

interface BottomBorderContainerProps {
  bottomBorderColor?: string;
  cssProp?: CSSProp;
}

interface InputProps {
  textAlign?: "left" | "center";
  cssProp?: CSSProp;
}

export const Input = styled.input<InputProps>(
  ({ textAlign, cssProp }) => css`
    display: block;
    width: 100%;
    border: none;
    text-align: ${textAlign ? textAlign : "left"};
    background: none;
    font-size: 12px;

    :focus {
      outline: none;
    }

    ${cssProp}
  `
);

export const RoundedInputContainer = styled.div<RoundedContainerProps>`
  display: flex;
  align-items: center;
  border-radius: 8px;
  padding: 0.6875rem 1.125rem;
  background-color: ${({ theme, backgroundColor }) => (backgroundColor ? backgroundColor : theme.color.secondaryColor)};
  transition: background-color 0.5s;
  cursor: text;
  :focus-within {
    background-color: ${({ theme }) => theme.color.tagItemColor};
  }
`;

export const BottomBorderInputContainer = styled.div<BottomBorderContainerProps>`
  display: flex;
  align-items: center;
  border-bottom: 2px solid
    ${({ theme, bottomBorderColor }) => (bottomBorderColor ? bottomBorderColor : theme.color.secondaryColor)};
  padding: 0.6875rem 1.125rem;
  transition: border-bottom-color 0.5s;
  cursor: text;
  :focus-within {
    border-bottom-color: ${({ theme }) => theme.color.tagItemColor};
  }
`;
