import styled from "styled-components";
import { Header } from "../../@styled/layout";

export const Container = styled(Header)<React.CSSProperties>`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.625rem 1.4375rem;
`;

export const GoBackLink = styled.a`
  transition: opacity 0.5s;
  margin-right: 0.9375rem;

  :hover {
    opacity: 0.5;
  }
`;

export const SearchInputWrapper = styled.div`
  flex-grow: 1;
`;
