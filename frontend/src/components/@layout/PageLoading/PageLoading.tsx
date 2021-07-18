import Loader, { Props as LoaderProps } from "../../@shared/Loader/Loader";
import { Container } from "./PageLoading.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  LoaderSize?: string;
  LoaderKind?: LoaderProps["kind"];
}

const PageLoading = ({ LoaderSize = "2rem", LoaderKind = "spinner", ...props }: Props) => {
  return (
    <Container {...props}>
      <Loader size={LoaderSize} kind={LoaderKind} />
    </Container>
  );
};

export default PageLoading;
