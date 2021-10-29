import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { CameraIcon } from "../../assets/icons";
import { LIMIT } from "../../constants/limits";
import useModal from "../../hooks/common/useModal";
import useProfileModificationForm from "../../hooks/service/useProfileModificationForm";
import AlertPortal from "../@layout/AlertPortal/AlertPortal";
import PageLoadingWithCover from "../@layout/PageLoadingWithCover/PageLoadingWithCover";
import Avatar from "../@shared/Avatar/Avatar";
import Button from "../@shared/Button/Button";
import PostTextEditor from "../PostTextEditor/PostTextEditor";
import { Container, Heading, Label, TextEditorCSS, TextEditorWrapper } from "./ProfileModificationForm.style";

export interface Props {
  username: string;
  profileImageUrl?: string;
  prevDescription?: string;
  onTerminate: () => void;
}

const ProfileModificationForm = ({ username, profileImageUrl, prevDescription, onTerminate }: Props) => {
  const theme = useContext(ThemeContext);
  const {
    modalMessage: alertMessage,
    isModalShown: isAlertShown,
    hideModal: hideAlert,
    showModal: showAlert,
  } = useModal();
  const { values, handlers, isLoading } = useProfileModificationForm(
    username,
    { imageUrl: profileImageUrl, description: prevDescription },
    showAlert,
    onTerminate
  );
  const { imageUrl, description } = values;
  const { handleImageChange, handleDescriptionChange, handleModificationSubmit } = handlers;

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
        <PostTextEditor
          cssProp={TextEditorCSS}
          placeholder="한 줄 소개"
          maxLength={LIMIT.PROFILE_DESCRIPTION_LENGTH}
          value={description}
          onChange={handleDescriptionChange}
          autoGrow={false}
        />
      </TextEditorWrapper>
      <Button kind="roundedBlock">수정 완료</Button>
      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
      {isLoading && <PageLoadingWithCover description="수정중" />}
    </Container>
  );
};

export default ProfileModificationForm;
