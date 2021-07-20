import { useContext, useState } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import ImageSlider from "../@shared/ImageSlider/ImageSlider";
import ImageUploader from "../@shared/ImageUploader/ImageUploader";
import TextEditor from "../@shared/TextEditor/TextEditor";
import { Container, ImageUploaderWrapper, TextEditorWrapper } from "./PostContentUploader.style";

const PostContentUploader = () => {
  const [imageUrls, setImageUrls] = useState<string[]>([]);
  const { content, setFiles, setContent } = useContext(PostAddDataContext);

  const handlePostContentChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    const { value } = event.target;
    setContent(value);
  };

  const handleFileListSave = (fileList: FileList) => {
    const files = Array.from(fileList);

    files.forEach((file) => {
      const imageUrl = URL.createObjectURL(file);
      setImageUrls((state) => [...state, imageUrl]);
    });

    setFiles(files);
  };

  return (
    <Container>
      {imageUrls.length > 0 ? (
        <ImageSlider imageUrls={imageUrls} slideButtonKind="stick-out" />
      ) : (
        <ImageUploaderWrapper>
          <ImageUploader onFileListSave={handleFileListSave} />
        </ImageUploaderWrapper>
      )}
      <TextEditorWrapper>
        <TextEditor
          height="100%"
          onChange={handlePostContentChange}
          value={content}
          placeholder="내용을 작성해주세요..."
          autoGrow
        />
      </TextEditorWrapper>
    </Container>
  );
};

export default PostContentUploader;
