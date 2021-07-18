import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 3.125rem 2.0625rem;
`;

export const SearchInputWrapper = styled.div`
  margin-bottom: 2.0625rem;
`;

export const RepositoryList = styled.ul`
  height: 400px;
  overflow-y: auto;
`;

export const RepositoryListItem = styled.li`
  padding: 0.5625rem 0.25rem;
  display: flex;
  align-items: center;
  transition: background-color 0.5s;
  cursor: pointer;

  ${({ theme }) => `
    border-bottom: 1px solid  ${theme.color.separatorColor};
    
    :hover {
      background-color: ${theme.color.secondaryColor};
    }
  `};
`;

export const RepositoryCircle = styled.a`
  margin-right: 1rem;
`;

export const RepositoryName = styled.span`
  font-weight: bold;
  color: ${({ theme }) => theme.color.textColor};
`;
