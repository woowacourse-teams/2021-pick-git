import { Dispatch, SetStateAction, useState } from "react";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/messages";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import { isValidFilesSize, isValidFilesSizeCount } from "../../utils/postUpload";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
import ImageSlider from "../@shared/ImageSlider/ImageSlider";
import ImageUploader from "../@shared/ImageUploader/ImageUploader";
import TextEditor from "../@shared/TextEditor/TextEditor";
import { Container, ImageUploaderWrapper, TextEditorWrapper } from "./PostContentUploader.style";

interface Props {
  isImageUploaderShown: boolean;
  content: string;
  setFiles?: Dispatch<SetStateAction<File[]>>;
  setContent: Dispatch<SetStateAction<string>>;
}

// TODO : key 를 넣지 않는 방법 생각해보기
const PostContentUploader = ({ isImageUploaderShown, content, setFiles, setContent }: Props) => {
  const [imageUrls, setImageUrls] = useState<string[]>([]);
  const { modalMessage, isModalShown, hideMessageModal, showAlertModal } = useMessageModal();

  const handlePostContentChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    const { value } = event.target;
    setContent(value);
  };

  const handleFileListSave = (fileList: FileList) => {
    const files = Array.from(fileList);

    if (!isValidFilesSizeCount(files)) {
      showAlertModal(FAILURE_MESSAGE.POST_FILE_COUNT_EXCEEDED);
      return;
    }

    if (!isValidFilesSize(files)) {
      showAlertModal(FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED);
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
        <TextEditor
          height="100%"
          onChange={handlePostContentChange}
          value={content}
          lineHeight="1.8rem"
          placeholder="내용을 작성해주세요..."
          autoGrow
          maxLength={LIMIT.POST_CONTENT_MAX_LENGTH}
        />
      </TextEditorWrapper>
      {isModalShown && (
        <MessageModalPortal heading={modalMessage} onConfirm={hideMessageModal} onClose={hideMessageModal} />
      )}
    </Container>
  );
};

export default PostContentUploader;
