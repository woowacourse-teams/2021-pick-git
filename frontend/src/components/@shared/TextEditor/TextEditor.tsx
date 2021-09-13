import { useEffect, useRef, useState } from "react";
import { CSSProp } from "styled-components";

import { TextArea } from "./TextEditor.style";

export interface TextEditorProps extends React.HTMLAttributes<HTMLTextAreaElement> {
  value: string;
  autoGrow: boolean;
  cssProp?: CSSProp;
}

const TextEditor = ({ value, cssProp, autoGrow, ...props }: TextEditorProps) => {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleKeyUp: React.KeyboardEventHandler<HTMLTextAreaElement> = (event) => {
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
      new Event("keyup", {
        bubbles: true,
      })
    );
  }, []);

  return (
    <TextArea
      value={value}
      cssProp={cssProp}
      ref={textareaRef}
      onKeyUp={autoGrow ? handleKeyUp : undefined}
      autoGrow={autoGrow}
      {...props}
    />
  );
};

export default TextEditor;
