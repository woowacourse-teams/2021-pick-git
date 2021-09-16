import { css } from "styled-components";
import TextEditor, { TextEditorProps } from "../@shared/TextEditor/TextEditor";
import { TextAreaCSS } from "./PortfolioTextEditor.style";

export interface Props extends TextEditorProps {}

const PortfolioTextEditor = ({ value, cssProp, placeholder, disabled, onChange }: Props) => {
  return (
    <TextEditor
      value={value}
      onChange={onChange}
      cssProp={css`
        ${cssProp}
        ${TextAreaCSS}
      `}
      autoGrow={true}
      placeholder={placeholder}
      disabled={disabled}
    />
  );
};

export default PortfolioTextEditor;
