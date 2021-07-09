import styled from "styled-components";

export const Container = styled.header<React.CSSProperties>`
  display: flex;
  justify-content: space-between;
  padding: 18px 24px;
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
  width: 22px;
`;
