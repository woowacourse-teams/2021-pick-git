import styled from "styled-components";

export const Container = styled.div<React.CSSProperties>`
  background-color: ${({ theme }) => theme.color.white};

  @media (min-width: 425px) {
    border: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;

export const PostItemWrapper = styled.div`
  margin-bottom: 2rem;
`;
