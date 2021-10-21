import { useRef } from "react";
import { CSSProp } from "styled-components";
import { Input as StyledInput, BottomBorderInputContainer, RoundedInputContainer, StyleProps } from "./Input.style";

export interface Props extends React.HTMLAttributes<HTMLInputElement>, StyleProps {
  kind?: "borderBottom" | "rounded";
  icon?: React.ReactNode;
  name?: string;
  value?: string;
  wrapperCssProp?: CSSProp;
  inputCssProp?: CSSProp;
  type?: string;
}

const Input = ({
  kind,
  icon,
  textAlign = "left",
  backgroundColor,
  bottomBorderColor,
  name,
  value,
  type,
  wrapperCssProp,
  inputCssProp,
  ...props
}: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const input = (
    <>
      {icon}
      <StyledInput
        name={name}
        ref={inputRef}
        textAlign={textAlign}
        type={type}
        autoComplete="off"
        value={value}
        style={icon ? { marginLeft: "0.9375rem" } : {}}
        {...props}
      />
    </>
  );

  const triggerInputFocus = () => {
    inputRef.current && inputRef.current.focus();
  };

  if (kind === "borderBottom") {
    return (
      <BottomBorderInputContainer
        onClick={triggerInputFocus}
        bottomBorderColor={bottomBorderColor}
        cssProp={wrapperCssProp}
      >
        {input}
      </BottomBorderInputContainer>
    );
  }

  if (kind === "rounded") {
    return (
      <RoundedInputContainer onClick={triggerInputFocus} backgroundColor={backgroundColor} cssProp={wrapperCssProp}>
        {input}
      </RoundedInputContainer>
    );
  }

  return (
    <BottomBorderInputContainer
      onClick={triggerInputFocus}
      bottomBorderColor={bottomBorderColor}
      cssProp={wrapperCssProp}
    >
      {input}
    </BottomBorderInputContainer>
  );
};

export default Input;
