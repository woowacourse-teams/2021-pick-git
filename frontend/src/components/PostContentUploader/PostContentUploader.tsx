import { Dispatch, SetStateAction, useState } from "react";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/messages";
import useModal from "../../hooks/common/useModal";
import { isValidFilesSize, isValidFilesSizeCount } from "../../utils/postUpload";
import AlertPortal from "../@layout/AlertPortal/AlertPortal";
import ImageSlider from "../@shared/ImageSlider/ImageSlider";
import ImageUploader from "../@shared/ImageUploader/ImageUploader";
import PostTextEditor from "../PostTextEditor/PostTextEditor";
import { Container, ImageUploaderWrapper, PostTextEditorCSS, TextEditorWrapper } from "./PostContentUploader.style";

interface Props {
  isImageUploaderShown: boolean;
  content: string;
  setFiles?: Dispatch<SetStateAction<File[]>>;
  setContent: Dispatch<SetStateAction<string>>;
}

// TODO : key 를 넣지 않는 방법 생각해보기
const PostContentUploader = ({ isImageUploaderShown, content, setFiles, setContent }: Props) => {
  const [imageUrls, setImageUrls] = useState<string[]>([]);
  const {
    isModalShown: isAlertShown,
    modalMessage: alertMessage,
    hideModal: hideAlert,
    showModal: showAlert,
  } = useModal();

  const handlePostContentChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    const { value } = event.target;
    setContent(value);
  };

  const handleFileListSave = (fileList: FileList) => {
    const files = Array.from(fileList);

    if (!isValidFilesSizeCount(files)) {
      showAlert(FAILURE_MESSAGE.POST_FILE_COUNT_EXCEEDED);
      return;
    }

    if (!isValidFilesSize(files)) {
      showAlert(FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED);
      return;
    }

    files.forEach((file) => {
      const imageUrl = URL.createObjectURL(file);
      setImageUrls((state) => [...state, imageUrl]);
    });

    setFiles && setFiles(files);
  };

  return (
    <Container>
      {isImageUploaderShown && imageUrls.length > 0 && (
        <ImageSlider imageUrls={imageUrls} slideButtonKind="stick-out" />
      )}
      {isImageUploaderShown && imageUrls.length === 0 && (
        <ImageUploaderWrapper>
          <ImageUploader onFileListSave={handleFileListSave} />
        </ImageUploaderWrapper>
      )}
      <TextEditorWrapper>
        <PostTextEditor
          onChange={handlePostContentChange}
          value={content}
          placeholder="내용을 작성해주세요..."
          maxLength={LIMIT.POST_CONTENT_MAX_LENGTH}
          cssProp={PostTextEditorCSS}
          autoGrow={false}
        />
      </TextEditorWrapper>
      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
    </Container>
  );
};

export default PostContentUploader;
