import { useEffect, useRef } from "react";
import Loader from "../Loader/Loader";
import { Container, LoaderWrapper, ContentWrapper } from "./InfiniteScrollContainer.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  isLoaderShown: boolean;
  onIntersect: () => void;
}

const InfiniteScrollContainer = ({ isLoaderShown, onIntersect, children }: Props) => {
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
      <LoaderWrapper ref={loaderRef}>{isLoaderShown && <Loader kind="dots" size="1rem" />}</LoaderWrapper>
    </Container>
  );
};

export default InfiniteScrollContainer;
