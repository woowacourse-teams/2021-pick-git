import { CSSProp } from "styled-components";
import { Input } from "./DateInput.style";

export interface Props {
  value: string;
  onChange: React.ChangeEventHandler<HTMLInputElement>;
  cssProp?: CSSProp;
}

const DateInput = ({ cssProp, value, onChange }: Props) => {
  return <Input cssProp={cssProp} type="date" value={value} onChange={onChange} />;
};

export default DateInput;
