import { ChangeEventHandler, KeyboardEventHandler, useState } from "react";

import TextArea from "./TextEditor.style";

export interface Props {
  width?: string;
  height?: string;
  backgroundColor?: string;
  fontSize?: string;
  autoGrow?: boolean;
  placeholder?: string;
  value: string;
  onChange: ChangeEventHandler<HTMLTextAreaElement>;
}

const TEXT_EDITOR_LINE_HEIGHT = 1.2;

const TextEditor = ({
  width,
  height,
  backgroundColor,
  fontSize = "1rem",
  autoGrow = false,
  placeholder,
  value,
  onChange,
}: Props) => {
  const [currentHeight, setCurrentHeight] = useState("");

  const onKeyUp: KeyboardEventHandler<HTMLTextAreaElement> = ({ key }) => {
    if (!autoGrow) return;

    if (key === "Enter" || key === "Backspace" || key === "Delete") {
      const lineCount = (value ?? "").split("\n").length + 1;
      const fontSizeNumber = fontSize.replace(/[^0-9]/g, "");
      const fontSizeMeasure = fontSize.replace(/[0-9]/g, "");

      setCurrentHeight(`${lineCount * Number(fontSizeNumber) * TEXT_EDITOR_LINE_HEIGHT}${fontSizeMeasure}`);
    }
  };

  return (
    <TextArea
      width={width}
      minHeight={height}
      height={currentHeight}
      backgroundColor={backgroundColor}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      onKeyUp={onKeyUp}
      fontSize={fontSize}
    />
  );
};

export default TextEditor;
