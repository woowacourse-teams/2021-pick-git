import styled from "styled-components";
import { Header } from "../../@styled/layout";

export const Container = styled(Header)<React.CSSProperties>`
  display: flex;
  justify-content: space-between;
  padding: 1.125rem 1.5rem;
`;

export const Content = styled.div`
  font-size: 1.0625rem;
`;

export const StepLink = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const EmptySpace = styled.div`
  display: inline-block;
  width: 1.375rem;
`;
