import styled from "styled-components";

export const Container = styled.div<React.CSSProperties>`
  border: 1px solid ${({ theme }) => theme.color.darkBorderColor};
`;

export const PostItemWrapper = styled.div`
  margin-bottom: 2rem;
`;
