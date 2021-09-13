import { LoadingDots, FirstLoadingDot, SecondLoadingDot, ThirdLoadingDot, Spinner } from "./Loader.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  kind: "dots" | "spinner";
  size: string;
  loaderColor?: string;
}

const Loader = ({ kind, size, loaderColor, ...props }: Props) => {
  if (kind === "spinner") {
    return <Spinner size={size} loaderColor={loaderColor} />;
  }

  return (
    <LoadingDots {...props}>
      <FirstLoadingDot size={size} loaderColor={loaderColor} />
      <SecondLoadingDot size={size} loaderColor={loaderColor} />
      <ThirdLoadingDot size={size} loaderColor={loaderColor} />
    </LoadingDots>
  );
};

export default Loader;
