import { SquaredInlineButton, SquaredBlockButton, RoundedInlineButton, RoundedBlockButton } from "./Button.style";

export interface Props extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  kind?: "squaredInline" | "squaredBlock" | "roundedInline" | "roundedBlock";
  backgroundColor?: string;
  color?: string;
}

const Button = ({ kind = "squaredInline", ...props }: Props) => {
  const button = {
    squaredInline: <SquaredInlineButton {...props} />,
    squaredBlock: <SquaredBlockButton {...props} />,
    roundedInline: <RoundedInlineButton {...props} />,
    roundedBlock: <RoundedBlockButton {...props} />,
  };

  return button[kind];
};

export default Button;
