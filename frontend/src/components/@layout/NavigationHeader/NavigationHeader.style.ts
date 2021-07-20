import { Link } from "react-router-dom";
import styled from "styled-components";
import { Header } from "../../@styled/layout";

export const Container = styled(Header)<React.CSSProperties>`
  display: flex;
  justify-content: space-between;
  padding: 1.0625rem 1.375rem;
`;

export const HomeLink = styled(Link)``;

export const Navigation = styled.nav`
  display: flex;
`;

export const NavigationItem = styled(Link)`
  transition: opacity 0.5s;

  :not(:last-child) {
    margin-right: 1.1875rem;
  }

  :hover {
    opacity: 0.5;
  }
`;

export const FlexWrapper = styled.div`
  display: flex;

  button {
    margin-left: 1.5rem;
  }
`;
