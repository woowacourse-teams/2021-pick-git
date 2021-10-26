import styled, { css } from "styled-components";
import { setLaptopAboveMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0rem 1.6875rem;
  height: 100%;

  ${setLaptopAboveMediaQuery`
    padding: 1.5rem 1.6875rem;
  `}
`;

export const ImageSliderWrapper = styled.div<{ isShown?: boolean }>(
  ({ isShown }) => css`
    width: 100%;
    display: ${isShown ? "block" : "none"};
    position: relative;
  `
);

export const ImageSliderCSS = css`
  height: 11.25rem;

  ${setLaptopAboveMediaQuery`
    height: 22.5rem;
  `}
`;

export const ImageUploaderCSS = css`
  height: 11.25rem;

  ${setLaptopAboveMediaQuery`
    height: 22.5rem;
  `}
`;

export const ReUploadButton = styled.button(
  ({ theme }) => css`
    position: absolute;
    right: 10px;
    bottom: 10px;
    background-color: ${theme.color.lighterTextColor};
    border-radius: 5px;
    padding: 8px 16px 10px 16px;
    font-size: 12px;
    color: ${theme.color.white};
    transition: background-color 0.5s;

    :hover {
      background-color: ${theme.color.tertiaryColor};
    }
  `
);

export const ImageUploaderWrapper = styled.div<{ isShown?: boolean }>(
  ({ isShown }) => css`
    display: ${isShown ? "block" : "none"};
    width: 100%;
  `
);

export const ImageChangeIconLink = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const TextEditorWrapper = styled.div(
  ({ theme }) => css`
    width: 100%;
    flex-grow: 1;
    padding-top: 1rem;
    background-color: ${theme.color.white};

    textarea {
      line-height: 1.5rem;
    }

    ${setLaptopAboveMediaQuery`
      padding-top: 3rem;
    
    `}
  `
);

export const PostTextEditorCSS = css`
  width: 100%;
  height: 100%;
`;
