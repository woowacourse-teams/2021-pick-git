import { useContext, useEffect, useState } from "react";
import { ThemeContext } from "styled-components";
import { CameraIcon } from "../../assets/icons";

import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/messages";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import { useProfileMutation } from "../../services/queries";
import { isValidFileSize, isValidProfileDescription } from "../../utils/profileModification";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
import PageLoading from "../@layout/PageLoading/PageLoading";
import Avatar from "../@shared/Avatar/Avatar";
import Button from "../@shared/Button/Button";
import TextEditor from "../@shared/TextEditor/TextEditor";
import { Container, Heading, Label, TextEditorWrapper } from "./ProfileModificationForm.style";

export interface Props {
  username: string;
  profileImageUrl?: string;
  prevDescription?: string;
  onTerminate: () => void;
}

const ProfileModificationForm = ({ username, profileImageUrl, prevDescription, onTerminate }: Props) => {
  const [image, setImage] = useState<File | null>(null);
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const [description, setDescription] = useState(prevDescription ?? "");

  const theme = useContext(ThemeContext);
  const { mutate, isLoading, isSuccess } = useProfileMutation(username);
  const { modalMessage, isModalShown, hideMessageModal, showAlertModal } = useMessageModal();

  const handleImageChange: React.ChangeEventHandler<HTMLInputElement> = ({ currentTarget: { files } }) => {
    if (!files) return;

    if (!isValidFileSize(files[0])) {
      showAlertModal(FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED);

      return;
    }

    setImage(files[0]);
    setImageUrl(URL.createObjectURL(files[0]));
  };

  const handleModificationSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!image && (!prevDescription || prevDescription === description)) {
      showAlertModal(FAILURE_MESSAGE.NO_CONTENT_MODIFIED);

      return;
    }

    if (image && !isValidFileSize(image)) {
      showAlertModal(FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED);

      return;
    }

    if (!isValidProfileDescription(description)) {
      showAlertModal(FAILURE_MESSAGE.PROFILE_DESCRIPTION_MAX_LENGTH_EXCEEDED);

      return;
    }

    mutate({ image, description });
  };

  const handleDescriptionChange: React.ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => {
    setDescription(value);
  };

  useEffect(() => {
    if (isSuccess) {
      onTerminate?.();
    }
  }, [isSuccess]);

  if (isLoading) {
    return (
      <Container>
        <PageLoading />
      </Container>
    );
  }

  return (
    <Container onSubmit={handleModificationSubmit}>
      <Heading>프로필 수정</Heading>
      <Label htmlFor="profile-image" avatarDiameter="5rem">
        <Avatar diameter="5rem" imageUrl={imageUrl ?? profileImageUrl} fontSize="1rem" name={username} />
        <CameraIcon color={theme.color.primaryColor} />
        <input
          type="file"
          id="profile-image"
          accept=".jpg, .png, .jpeg, tiff"
          style={{ display: "none" }}
          onChange={handleImageChange}
        />
      </Label>
      <TextEditorWrapper>
        <TextEditor
          height="100%"
          fontSize="1rem"
          placeholder="한 줄 소개"
          maxLength={LIMIT.PROFILE_DESCRIPTION_LENGTH}
          value={description}
          onChange={handleDescriptionChange}
        />
      </TextEditorWrapper>
      <Button kind="roundedBlock" padding="0.875rem">
        수정 완료
      </Button>
      {isModalShown && (
        <MessageModalPortal heading={modalMessage} onConfirm={hideMessageModal} onClose={hideMessageModal} />
      )}
    </Container>
  );
};

export default ProfileModificationForm;
