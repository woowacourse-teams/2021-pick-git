import { RefObject, useEffect, useState } from "react";
import { getScrollYPosition } from "../../utils/layout";

const useScrollPagination = (containerRef: RefObject<HTMLDivElement>, paginationCount: number) => {
  const [activePageIndex, setActivePageIndex] = useState(0);

  const paginate = (index: number) => {
    if (index < 0 || index >= paginationCount) {
      return;
    }

    setActivePageIndex(index);
  };

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    containerRef.current.scrollTo({
      behavior: "smooth",
      top: getScrollYPosition(containerRef.current.children[activePageIndex], containerRef.current),
    });
  }, [activePageIndex]);

  // useEffect(() => {
  //   if (!containerRef.current) {
  //     return;
  //   }

  //   const observer = new IntersectionObserver(
  //     (entries) => {
  //       entries.forEach((entry, index) => {
  //         if (index === entries.length - 1) {
  //           paginate(Number(entry.target.getAttribute("data-index")));
  //         }
  //       });
  //     },
  //     { threshold: 0.97 }
  //   );

  //   Array.from(containerRef.current.children).forEach((child) => {
  //     (child);
  //     observer.observe(child);
  //   });
  // }, [containerRef.current]);

  return {
    activePageIndex,
    paginate,
  };
};

export default useScrollPagination;
