import { RefObject, useEffect, useState } from "react";
import { getScrollYPosition } from "../../utils/layout";
import useDebounce from "./useDebounce";

const useScrollPagination = (containerRef: RefObject<HTMLDivElement>, paginationCount: number) => {
  const [activePageIndex, setActivePageIndex] = useState(0);

  console.log(activePageIndex);

  const paginate = (index: number) => {
    if (index < 0 || index >= paginationCount) {
      return;
    }

    setActivePageIndex(index);
  };

  const increasePageIndex = useDebounce(() => {
    paginate(activePageIndex + 1);
  }, 500);

  const decreasePageIndex = useDebounce(() => {
    paginate(activePageIndex - 1);
  }, 500);

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

    const preventWheelEvent = (event: WheelEvent) => {
      event.preventDefault();

      console.log("event.deltaY", event.deltaY);

      if (event.deltaY > 0) {
        increasePageIndex();
      } else {
        decreasePageIndex();
      }
    };

    containerRef.current.addEventListener("wheel", preventWheelEvent, {
      passive: false,
    });

    return () => {
      if (!containerRef.current) {
        return;
      }

      containerRef.current.removeEventListener("wheel", preventWheelEvent);
    };
  }, [activePageIndex]);

  return {
    activePageIndex,
    paginate,
  };
};

export default useScrollPagination;
