import { useEffect, useRef } from "react";
import Loader from "../Loader/Loader";
import { Container, LoaderWrapper, ContentWrapper } from "./InfiniteScrollContainer.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  onIntersect: () => void;
}

const InfiniteScrollContainer = ({ children, onIntersect }: Props) => {
  const loaderRef = useRef<HTMLDivElement>(null);

  const observer = new IntersectionObserver((entries) => {
    const [entry] = entries;

    if (!entry.isIntersecting) {
      return;
    }

    onIntersect();
  });

  useEffect(() => {
    loaderRef.current && observer.observe(loaderRef.current);
  }, [loaderRef]);

  return (
    <Container>
      <ContentWrapper>{children}</ContentWrapper>
      <LoaderWrapper ref={loaderRef}>
        <Loader kind="dots" size="1rem" />
      </LoaderWrapper>
    </Container>
  );
};

export default InfiniteScrollContainer;
