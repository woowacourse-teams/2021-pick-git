import { RoundedBlockButton, SquareInlineButton } from "./Button.style";

export interface Props extends React.HTMLAttributes<HTMLButtonElement> {
  kind?: "roundedBlock" | "inlineSquare";
}

const Button = ({ kind = "inlineSquare", ...props }: Props) => {
  if (kind === "roundedBlock") {
    return <RoundedBlockButton {...props}></RoundedBlockButton>;
  }

  return <SquareInlineButton {...props}></SquareInlineButton>;
};

export default Button;
