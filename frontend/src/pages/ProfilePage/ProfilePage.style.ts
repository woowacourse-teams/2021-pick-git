import styled from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)`
  background-color: ${({ theme }) => theme.color.white};
  height: fit-content;
  min-height: 100vh;
  overflow-y: auto;
`;

export const LoadingWrapper = styled.div`
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
`;
