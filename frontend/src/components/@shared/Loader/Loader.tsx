import { CSSProp } from "styled-components";
import { LoadingDots, FirstLoadingDot, SecondLoadingDot, ThirdLoadingDot, Spinner } from "./Loader.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  kind: "dots" | "spinner";
  size: string;
  loaderColor?: string;
  cssProp?: CSSProp;
}

const Loader = ({ kind, size, loaderColor, cssProp, ...props }: Props) => {
  if (kind === "spinner") {
    return <Spinner cssProp={cssProp} size={size} loaderColor={loaderColor} />;
  }

  return (
    <LoadingDots cssProp={cssProp} {...props}>
      <FirstLoadingDot size={size} loaderColor={loaderColor} />
      <SecondLoadingDot size={size} loaderColor={loaderColor} />
      <ThirdLoadingDot size={size} loaderColor={loaderColor} />
    </LoadingDots>
  );
};

export default Loader;
