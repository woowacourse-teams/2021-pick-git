import { useState } from "react";
import { FAILURE_MESSAGE } from "../../constants/messages";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import usePostUpload from "../../services/hooks/usePostUpload";
import { isValidFilesSize, isValidFilesSizeCount } from "../../utils/postUpload";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
import ImageSlider from "../@shared/ImageSlider/ImageSlider";
import ImageUploader from "../@shared/ImageUploader/ImageUploader";
import TextEditor from "../@shared/TextEditor/TextEditor";
import { Container, ImageUploaderWrapper, TextEditorWrapper } from "./PostContentUploader.style";

const PostContentUploader = () => {
  const [imageUrls, setImageUrls] = useState<string[]>([]);
  const { content, setFiles, setContent } = usePostUpload();
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
      {isModalShown && (
        <MessageModalPortal heading={modalMessage} onConfirm={hideMessageModal} onClose={hideMessageModal} />
      )}
    </Container>
  );
};

export default PostContentUploader;
