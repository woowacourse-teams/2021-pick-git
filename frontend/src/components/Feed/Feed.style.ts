import styled from "styled-components";

export const Container = styled.div<React.CSSProperties>`
  @media (min-width: 375px) {
    border: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;

export const PostItemWrapper = styled.div`
  margin-bottom: 2rem;
`;
