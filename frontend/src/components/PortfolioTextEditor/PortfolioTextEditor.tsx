import { css } from "styled-components";
import TextEditor, { TextEditorProps } from "../@shared/TextEditor/TextEditor";
import { TextAreaCSS } from "./PortfolioTextEditor.style";

export interface Props extends TextEditorProps {}

const PortfolioTextEditor = ({ value, cssProp, placeholder, disabled, autoGrow, onChange }: Props) => {
  return (
    <TextEditor
      value={value}
      onChange={onChange}
      cssProp={css`
        ${TextAreaCSS}
        ${cssProp}
      `}
      autoGrow={autoGrow}
      placeholder={placeholder}
      disabled={disabled}
    />
  );
};

export default PortfolioTextEditor;
