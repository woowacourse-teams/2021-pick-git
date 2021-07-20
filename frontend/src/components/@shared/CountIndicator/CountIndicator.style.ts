import styled from "styled-components";

export const Container = styled.div`
  display: inline-flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;

  width: fit-content;
  height: 2.6875rem;
  line-height: 0.9;
  color: ${({ theme }) => theme.color.textColor};
`;

export const Count = styled.div`
  font-size: 1rem;
  font-weight: bold;
`;

export const Name = styled.div`
  font-size: 0.875rem;
`;
