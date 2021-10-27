import { CSSProp } from "styled-components";
import { Input } from "./DateInput.style";

export interface Props extends React.HTMLAttributes<HTMLInputElement> {
  value: string;
  cssProp?: CSSProp;
  disabled?: boolean;
  onChange: React.ChangeEventHandler<HTMLInputElement>;
}

const DateInput = ({ cssProp, value, disabled, onChange, ...props }: Props) => {
  return <Input cssProp={cssProp} type="date" value={value} onChange={onChange} disabled={disabled} {...props} />;
};

export default DateInput;
