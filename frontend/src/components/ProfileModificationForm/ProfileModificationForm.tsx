import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { CameraIcon } from "../../assets/icons";
import { LIMIT } from "../../constants/limits";
import SnackBarContext from "../../contexts/SnackbarContext";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import useProfileModificationForm from "../../services/hooks/useProfileModificationForm";
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
  const theme = useContext(ThemeContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { modalMessage, isModalShown, hideMessageModal, showAlertModal } = useMessageModal();
  const { values, handlers, isLoading } = useProfileModificationForm(
    username,
    { imageUrl: profileImageUrl, description: prevDescription },
    showAlertModal,
    onTerminate
  );
  const { imageUrl, description } = values;
  const { handleImageChange, handleDescriptionChange, handleModificationSubmit } = handlers;

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
        <Avatar diameter="5rem" imageUrl={imageUrl} fontSize="1rem" name={username} />
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
