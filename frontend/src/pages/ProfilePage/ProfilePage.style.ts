import styled from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)`
  background-color: ${({ theme }) => theme.color.white};
  min-height: 100vh;

  @media (min-width: 425px) {
    border: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;
