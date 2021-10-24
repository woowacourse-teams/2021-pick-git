import styled, { css } from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)<React.CSSProperties>(
  ({ theme }) => css`
    display: flex;
    flex-direction: column;
    color: ${theme.color.textColor};
  `
);

export const ContentWrapper = styled.div`
  flex-grow: 1;
  padding: 1.4375rem;
`;

export const NotFoundCSS = css`
  margin-top: 3rem;
`;

export const Empty = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
`;

export const KeywordsWrapper = styled.div`
  span {
    margin: 0.3rem;
  }
`;
