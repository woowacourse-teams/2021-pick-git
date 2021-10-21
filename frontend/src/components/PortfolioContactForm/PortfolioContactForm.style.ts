import styled, { css } from "styled-components";
import { customScrollbarCSS } from "../@styled/scrollbar";

export const Container = styled.div(
  ({ theme }) => css`
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    width: 100%;
    height: 100%;
    padding: 1rem 2rem;
    overflow-y: scroll;

    ${customScrollbarCSS(theme.color.tagItemColor)}
  `
);

export const ContactInputBlockWrapper = styled.div``;

export const ContactInputBlock = styled.div(
  () => css`
    margin-bottom: 1.5rem;
  `
);

export const ContactLabel = styled.label(
  ({ theme }) => css`
    padding: 1.25rem;
    color: ${theme.color.textColor};
  `
);

export const ContactInputCSS = css``;

export const ContactInputCompleteButtonCSS = css``;
