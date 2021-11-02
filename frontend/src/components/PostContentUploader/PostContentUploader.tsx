import { Dispatch, SetStateAction, useRef, useState } from "react";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/messages";
import useModal from "../../hooks/common/useModal";
import { isValidFilesSize, isValidFilesSizeCount } from "../../utils/postUpload";
import AlertPortal from "../@layout/AlertPortal/AlertPortal";
import ImageSlider from "../@shared/ImageSlider/ImageSlider";
import ImageUploader from "../@shared/ImageUploader/ImageUploader";
import PostTextEditor from "../PostTextEditor/PostTextEditor";
import {
  Container,
  ImageSliderCSS,
  ImageSliderWrapper,
  ImageUploaderCSS,
  ImageUploaderWrapper,
  PostTextEditorCSS,
  ReUploadButton,
  TextEditorWrapper,
} from "./PostContentUploader.style";

interface Props {
  isImageUploaderShown: boolean;
  content: string;
  setFiles?: Dispatch<SetStateAction<File[]>>;
  setContent: Dispatch<SetStateAction<string>>;
}

const PostContentUploader = ({ isImageUploaderShown, content, setFiles, setContent }: Props) => {
  const uploaderRef = useRef<HTMLImageElement>(null);
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

    const newImageUrls = files.map((file) => URL.createObjectURL(file));
    setImageUrls(newImageUrls);

    setFiles && setFiles(files);
  };

  const handleReUploadButtonClick = () => {
    if (!uploaderRef.current) {
      return;
    }

    uploaderRef.current.click();
  };

  return (
    <Container>
      <ImageSliderWrapper isShown={isImageUploaderShown && imageUrls.length > 0}>
        <ImageSlider cssProp={ImageSliderCSS} imageUrls={imageUrls} slideButtonKind="stick-out" />
        <ReUploadButton onClick={handleReUploadButtonClick}>다시 올리기</ReUploadButton>
      </ImageSliderWrapper>
      <ImageUploaderWrapper isShown={isImageUploaderShown && imageUrls.length === 0}>
        <ImageUploader cssProp={ImageUploaderCSS} onFileListSave={handleFileListSave} imageUploaderRef={uploaderRef} />
      </ImageUploaderWrapper>
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
