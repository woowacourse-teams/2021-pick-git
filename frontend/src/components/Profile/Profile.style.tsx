import styled from "styled-components";

export const Container = styled.section`
  padding: 1.4219rem;
`;

export const Description = styled.p`
  ${({ theme }) => `
    color: ${theme.color.textColor};
    border-left: 3px solid ${theme.color.primaryColor};
  `}
  font-size: 0.7rem;
  margin: 1.25rem 0;
  padding: 0.2rem 0.5rem;
  line-height: 1;
`;

export const DetailInfo = styled.div`
  display: flex;
  align-items: center;

  width: 100%;
  color: ${({ theme }) => theme.color.textColor};
  font-size: 0.875rem;
  line-height: 2.5;

  svg {
    margin-right: 1rem;
  }
`;
