import styled from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)<React.CSSProperties>`
  background-color: ${({ theme }) => theme.color.white};
  padding-left: 1.4375rem;
  padding-right: 1.4375rem;
  height: 100vh;
`;

export const Empty = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
`;
