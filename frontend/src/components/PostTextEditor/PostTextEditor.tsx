import TextEditor, { TextEditorProps } from "../@shared/TextEditor/TextEditor";
import { Container, TextEditorCSS, TextLengthIndicator } from "./PostTextEditor.style";

export interface Props extends TextEditorProps {
  maxLength: number;
}

const PostTextEditor = ({ value, maxLength, cssProp, autoGrow, onChange }: Props) => {
  return (
    <Container cssProp={cssProp}>
      <TextEditor value={value} onChange={onChange} autoGrow={autoGrow} cssProp={TextEditorCSS} />
      {maxLength && <TextLengthIndicator>{`${value.length} / ${maxLength}`}</TextLengthIndicator>}
    </Container>
  );
};

export default PostTextEditor;
