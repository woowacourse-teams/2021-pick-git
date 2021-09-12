import { RefObject, useEffect, useState } from "react";
import useDebounce from "../../../services/hooks/@common/useDebounce";
import { Container } from "./ScrollActiveHeader.style";

export interface Props {
  containerRef: RefObject<HTMLDivElement>;
  children: React.ReactNode;
}

const ScrollActiveHeader = ({ containerRef, children }: Props) => {
  const [isHeaderShown, setIsHeaderShown] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);

  const activateHeader = useDebounce(() => {
    if (!containerRef.current) {
      return;
    }

    const currentScrollY = containerRef.current.scrollTop;

    if (currentScrollY <= lastScrollY) {
      setIsHeaderShown(true);
    } else {
      setIsHeaderShown(false);
    }

    setLastScrollY(currentScrollY);
  }, 80);

  const toggleHeader = useDebounce(() => {
    setIsHeaderShown(!isHeaderShown);
  }, 200);

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    containerRef.current.onscroll = activateHeader;
    // containerRef.current.onclick = toggleHeader;
  }, [lastScrollY, isHeaderShown]);

  return (
    <Container ref={containerRef} isHeaderShown={isHeaderShown}>
      {children}
    </Container>
  );
};

export default ScrollActiveHeader;
