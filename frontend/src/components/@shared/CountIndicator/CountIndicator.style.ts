import styled, { css } from "styled-components";

export const Container = styled.div`
  display: inline-flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;

  width: fit-content;
  height: 2.6875rem;
  line-height: 0.9;
`;

export const Count = styled.div(
  ({ theme }) => css`
    font-size: 1rem;
    font-weight: bold;
    color: ${theme.color.textColor};
  `
);

export const Name = styled.div(
  ({ theme }) => css`
    font-size: 0.875rem;
    color: ${theme.color.textColor};
  `
);
