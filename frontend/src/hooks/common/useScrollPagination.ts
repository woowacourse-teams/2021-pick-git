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

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry, index) => {
          console.log("index", index);
          console.log("entry", entry.intersectionRatio);
          if (entry.intersectionRatio < 0.7) {
            return;
          }

          paginate(index);
        });
      },
      { threshold: 0.5 }
    );

    Array.from(containerRef.current.children).forEach((child) => {
      console.log(child);
      observer.observe(child);
    });
  }, [containerRef.current]);

  return {
    activePageIndex,
    paginate,
  };
};

export default useScrollPagination;
