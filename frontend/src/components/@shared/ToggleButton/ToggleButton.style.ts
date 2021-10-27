import styled, { css, CSSProp } from "styled-components";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    display: flex;
    align-items: center;
    border: none;
    ${cssProp}
  `
);

export const ToggleButtonText = styled.span(
  ({ theme }) => css`
    color: ${theme.color.textColor};
    margin-right: 0.75rem;
  `
);

export const Switch = styled.label`
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
`;

export const Checkbox = styled.input(
  ({ theme }) => css`
    display: none;

    :checked + span {
      background-color: ${theme.color.primaryColor};

      ::before {
        transform: translateX(1.25rem);
        background-color: ${theme.color.white};
      }
    }
  `
);

export const Slider = styled.span(
  ({ theme }) => css`
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: ${theme.color.tagItemColor};
    transition: all 0.5s;
    border-radius: 3rem;

    ::before {
      position: absolute;
      content: "";
      height: 18px;
      width: 18px;
      left: 3px;
      bottom: 3px;
      background-color: ${theme.color.white};
      border-radius: 50%;

      transition: all 0.5s;
    }
  `
);
