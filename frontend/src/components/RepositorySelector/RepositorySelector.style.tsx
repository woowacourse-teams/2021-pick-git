import { Link } from "react-router-dom";
import styled, { css } from "styled-components";
import { fadeIn } from "../@styled/keyframes";
import { customScrollbarCSS } from "../@styled/scrollbar";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 3.125rem 2.0625rem 0;
`;

export const SearchInputWrapper = styled.div`
  margin-bottom: 2.0625rem;
`;

export const RepositoryList = styled.ul(
  ({ theme }) => css`
    height: auto;
    overflow-y: auto;

    ${customScrollbarCSS(theme.color.textColor)}
  `
);

export const RepositoryListItem = styled.li(
  ({ theme }) => css`
    padding: 0.5625rem 0.25rem;
    display: flex;
    align-items: center;
    transition: background-color 0.5s;
    cursor: pointer;
    border-bottom: 1px solid ${theme.color.separatorColor};
    animation: ${fadeIn} 1s forwards;

    :hover {
      background-color: ${theme.color.secondaryColor};
    }
  `
);

export const RepositoryCircle = styled.a`
  margin-right: 1rem;
`;

export const RepositoryName = styled.span`
  font-weight: bold;
  color: ${({ theme }) => theme.color.textColor};
`;

export const SearchResultNotFound = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
`;

export const GoBackLink = styled(Link)`
  margin-top: 2rem;
  font-weight: bold;
  color: ${({ theme }) => theme.color.primaryColor};
`;
