import { useState } from "react";
import { Container, DotPagination } from "./DotPaginator.style";

export interface Props {
  paginationCount: number;
  paginate: (index: number) => void;
}

const DotPaginator = ({ paginationCount, paginate }: Props) => {
  const [activePageIndex, setActivePageIndex] = useState(0);

  const handlePaginate = (index: number) => {
    setActivePageIndex(index);
    paginate(index);
  };

  const paginationItems = [...Array(paginationCount)].map((_, index) => (
    <DotPagination key={index} isActive={activePageIndex === index} onClick={() => handlePaginate(index)} />
  ));

  return <Container>{paginationItems}</Container>;
};

export default DotPaginator;
