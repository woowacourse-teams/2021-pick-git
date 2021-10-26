import { useEffect, useRef, useState } from "react";
import { LayoutInPx } from "../../constants/layout";

const useAutoAnchor = (htmlId?: string) => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const [mountCounter, setMountCounter] = useState(0);
  const scrollWrapperRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!htmlId) {
      return;
    }

    if (!isMountedOnce) {
      setMountCounter((prev) => prev + 1);
      setIsMountedOnce(scrollWrapperRef.current !== null);

      return;
    }

    const $targetPost = document.querySelector(`#post${htmlId}`);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      scrollWrapperRef.current?.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [htmlId, mountCounter, isMountedOnce]);

  return { scrollWrapperRef };
};

export default useAutoAnchor;
