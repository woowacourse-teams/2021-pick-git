import { CSSProp } from "styled-components";
import { Input } from "./DateInput.style";

export interface Props {
  value: string;
  onChange: () => void;
  cssProp?: CSSProp;
}

const DateInput = ({ cssProp, value, onChange }: Props) => {
  return <Input cssProp={cssProp} type="date" />;
};

export default DateInput;
