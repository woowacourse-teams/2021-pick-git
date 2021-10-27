import { useEffect, useRef, useState } from "react";
import { CSSProp } from "styled-components";

import { TextArea } from "./TextEditor.style";

export interface TextEditorProps extends React.HTMLAttributes<HTMLTextAreaElement> {
  value: string;
  autoGrow: boolean;
  cssProp?: CSSProp;
  disabled?: boolean;
}

const TextEditor = ({ value, cssProp, autoGrow, ...props }: TextEditorProps) => {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleInput: React.KeyboardEventHandler<HTMLTextAreaElement> = (event) => {
    if (!textareaRef.current) {
      return;
    }

    textareaRef.current.style.height = `auto`;
    const { scrollHeight } = event.currentTarget;
    textareaRef.current.style.height = `${scrollHeight}px`;
  };

  useEffect(() => {
    if (!textareaRef.current) {
      return;
    }

    textareaRef.current.dispatchEvent(
      new Event("input", {
        bubbles: true,
      })
    );
  }, []);

  return (
    <TextArea
      value={value}
      cssProp={cssProp}
      ref={textareaRef}
      onInput={autoGrow ? handleInput : undefined}
      autoGrow={autoGrow}
      {...props}
    />
  );
};

export default TextEditor;
