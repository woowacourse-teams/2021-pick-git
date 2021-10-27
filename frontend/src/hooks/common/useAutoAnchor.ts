import { useEffect, useRef, useState } from "react";
import { LayoutInPx } from "../../constants/layout";

const useAutoAnchor = (selector?: string) => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const [mountCounter, setMountCounter] = useState(0);
  const scrollWrapperRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!selector) {
      return;
    }

    if (!isMountedOnce) {
      setMountCounter((prev) => prev + 1);
      setIsMountedOnce(scrollWrapperRef.current !== null);

      return;
    }

    const $targetPost = document.querySelector(selector);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      scrollWrapperRef.current?.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [selector, mountCounter, isMountedOnce]);

  return { scrollWrapperRef };
};

export default useAutoAnchor;
