import { useState } from "react";

import { Container, TextArea, TextLengthIndicator } from "./TextEditor.style";

export interface Props {
  width?: string;
  height?: string;
  fontSize?: string;
  autoGrow?: boolean;
  placeholder?: string;
  maxLength?: number;
  value: string;
  onChange: React.ChangeEventHandler<HTMLTextAreaElement>;
}

const TEXT_EDITOR_LINE_HEIGHT = 1.2;

const TextEditor = ({
  width,
  height,
  fontSize = "1rem",
  autoGrow = false,
  placeholder,
  maxLength,
  value,
  onChange,
}: Props) => {
  const [currentHeight, setCurrentHeight] = useState("");

  const handleKeyUp: React.KeyboardEventHandler<HTMLTextAreaElement> = ({ key }) => {
    if (!autoGrow) return;

    if (key === "Enter" || key === "Backspace" || key === "Delete") {
      const lineCount = (value ?? "").split("\n").length + 1;
      const fontSizeNumber = fontSize.replace(/[^0-9]/g, "");
      const fontSizeMeasure = fontSize.replace(/[0-9]/g, "");

      setCurrentHeight(`${lineCount * Number(fontSizeNumber) * TEXT_EDITOR_LINE_HEIGHT}${fontSizeMeasure}`);
    }
  };

  const handleChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    if (maxLength && event.target.value.length > maxLength) {
      return;
    }

    onChange(event);
  };

  return (
    <Container height={currentHeight} minHeight={height}>
      <TextArea
        width={width}
        minHeight={height}
        height={currentHeight}
        placeholder={placeholder}
        value={value}
        onChange={handleChange}
        onKeyUp={handleKeyUp}
        fontSize={fontSize}
      />
      {maxLength && <TextLengthIndicator>{`${value.length} / ${maxLength}`}</TextLengthIndicator>}
    </Container>
  );
};

export default TextEditor;
