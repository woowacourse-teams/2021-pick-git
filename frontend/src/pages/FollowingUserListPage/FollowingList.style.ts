import styled from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)<React.CSSProperties>`
  background-color: ${({ theme }) => theme.color.white};
  padding: 0 1.4375rem;
  height: 100vh;
`;
