import { useState } from "react";
import { Container, DotPagination } from "./DotPaginator.style";

export interface Props {
  activePageIndex: number;
  paginationCount: number;
  onPaginate: (index: number) => void;
}

const DotPaginator = ({ activePageIndex, paginationCount, onPaginate }: Props) => {
  const paginationItems = [...Array(paginationCount)].map((_, index) => (
    <DotPagination key={index} isActive={activePageIndex === index} onClick={() => onPaginate(index)} />
  ));

  return <Container>{paginationItems}</Container>;
};

export default DotPaginator;
