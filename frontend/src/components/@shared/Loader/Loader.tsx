import { LoadingDots, FirstLoadingDot, SecondLoadingDot, ThirdLoadingDot, Spinner } from "./Loader.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  kind: "dots" | "spinner";
  size: string;
}

const Loader = ({ kind, size, ...props }: Props) => {
  if (kind === "spinner") {
    return <Spinner size={size} />;
  }

  return (
    <LoadingDots {...props}>
      <FirstLoadingDot size={size} />
      <SecondLoadingDot size={size} />
      <ThirdLoadingDot size={size} />
    </LoadingDots>
  );
};

export default Loader;
