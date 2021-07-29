import { useRef } from "react";
import { Input as StyledInput, BottomBorderInputContainer, RoundedInputContainer, StyleProps } from "./Input.style";

export interface Props extends React.HTMLAttributes<HTMLInputElement>, StyleProps {
  kind?: "borderBottom" | "rounded";
  icon?: React.ReactNode;
  name?: string;
  value?: string;
}

const Input = ({
  kind,
  icon,
  textAlign = "left",
  backgroundColor,
  bottomBorderColor,
  name,
  value,
  ...props
}: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const input = (
    <>
      {icon}
      <StyledInput
        name={name}
        type="text"
        ref={inputRef}
        textAlign={textAlign}
        value={value}
        {...props}
        style={icon ? { marginLeft: "0.9375rem" } : {}}
      />
    </>
  );

  const triggerInputFocus = () => {
    inputRef.current && inputRef.current.focus();
  };

  if (kind === "borderBottom") {
    return (
      <BottomBorderInputContainer onClick={triggerInputFocus} bottomBorderColor={bottomBorderColor}>
        {input}
      </BottomBorderInputContainer>
    );
  }

  if (kind === "rounded") {
    return (
      <RoundedInputContainer onClick={triggerInputFocus} backgroundColor={backgroundColor}>
        {input}
      </RoundedInputContainer>
    );
  }

  return (
    <BottomBorderInputContainer onClick={triggerInputFocus} bottomBorderColor={bottomBorderColor}>
      {input}
    </BottomBorderInputContainer>
  );
};

export default Input;
