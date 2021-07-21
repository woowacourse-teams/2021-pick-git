import styled from "styled-components";

export const Container = styled.div<{ columnCount: number; rowCount: number }>`
  display: grid;
  width: 100%;
  height: 100%;
  ${({ columnCount, rowCount }) => `
    grid-template-columns: repeat(${columnCount}, 1fr);
    grid-template-rows: repeat(${rowCount}, 1fr);
  `};
  grid-column-gap: 0.1875rem;
  grid-row-gap: 0.1875rem;
  grid-auto-flow: column;
`;

export const ContributionItem = styled.div<{ backgroundColor: string }>`
  background-color: ${({ backgroundColor }) => backgroundColor};
  border-radius: 3px;
`;
