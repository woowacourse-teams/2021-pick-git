import styled from "styled-components";

export const Container = styled.div`
  @media (min-width: 375px) {
    border: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;
