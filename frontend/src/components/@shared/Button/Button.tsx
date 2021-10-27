import { CSSProp } from "styled-components";
import { SquaredInlineButton, SquaredBlockButton, RoundedInlineButton, RoundedBlockButton } from "./Button.style";

export interface Props extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  kind?: "squaredInline" | "squaredBlock" | "roundedInline" | "roundedBlock";
  backgroundColor?: string;
  color?: string;
  padding?: string;
  cssProp?: CSSProp;
}

const Button = ({ kind = "squaredInline", cssProp, ...props }: Props) => {
  const button = {
    squaredInline: <SquaredInlineButton cssProp={cssProp} {...props} />,
    squaredBlock: <SquaredBlockButton cssProp={cssProp} {...props} />,
    roundedInline: <RoundedInlineButton cssProp={cssProp} {...props} />,
    roundedBlock: <RoundedBlockButton cssProp={cssProp} {...props} />,
  };

  return button[kind];
};

export default Button;
