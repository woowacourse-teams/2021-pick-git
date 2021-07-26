import { useContext } from "react";
import { ThemeContext } from "styled-components";
import { ProfileData } from "../../../@types";

import UserContext from "../../../contexts/UserContext";
import useModal from "../../../services/hooks/@common/useModal";
import { useFollowingMutation, useUnfollowingMutation } from "../../../services/queries";
import ModalPortal from "../../@layout/Modal/ModalPortal";
import ProfileModificationForm from "../../ProfileModificationForm/ProfileModificationForm";
import Avatar from "../Avatar/Avatar";
import Button from "../Button/Button";
import CountIndicator from "../CountIndicator/CountIndicator";
import { ButtonLoader, ButtonSpinner, Container, Indicators } from "./ProfileHeader.style";

export interface Props {
  isMyProfile: boolean;
  profile: ProfileData | null;
}

const ProfileHeader = ({ isMyProfile, profile }: Props) => {
  const theme = useContext(ThemeContext);
  const { isLoggedIn } = useContext(UserContext);
  const { isModalShown, showModal, hideModal } = useModal(false);

  const { mutate: follow, isLoading: isFollowLoading } = useFollowingMutation(profile?.name);
  const { mutate: unFollow, isLoading: isUnfollowLoading } = useUnfollowingMutation(profile?.name);

  const onFollowButtonClick = () => {
    if (profile?.following === null) return;

    if (profile?.following) {
      unFollow();
    } else {
      follow();
    }
  };

  const ProfileButton = () => {
    if (!isLoggedIn) {
      return <></>;
    }

    if (isFollowLoading || isUnfollowLoading) {
      return (
        <ButtonLoader type="button" kind="squaredBlock" backgroundColor={theme.color.tertiaryColor}>
          {isFollowLoading ? "팔로우" : "팔로우 취소"}
          <ButtonSpinner size="1rem" />
        </ButtonLoader>
      );
    }

    if (isMyProfile) {
      return (
        <Button type="button" kind="squaredBlock" onClick={showModal}>
          프로필 수정
        </Button>
      );
    }

    if (profile?.following) {
      return (
        <Button
          type="button"
          kind="squaredBlock"
          backgroundColor={theme.color.tertiaryColor}
          onClick={onFollowButtonClick}
        >
          팔로우 취소
        </Button>
      );
    } else {
      return (
        <Button type="button" kind="squaredBlock" onClick={onFollowButtonClick}>
          팔로우
        </Button>
      );
    }
  };

  return (
    <Container>
      <Avatar diameter="3.75rem" fontSize="0.875rem" imageUrl={profile?.imageUrl} name={profile?.name} />
      <div>
        <Indicators>
          <CountIndicator name="게시물" count={profile?.postCount ?? 0} />
          <CountIndicator name="팔로워" count={profile?.followerCount ?? 0} />
          <CountIndicator name="팔로잉" count={profile?.followingCount ?? 0} />
        </Indicators>
        <ProfileButton />
      </div>
      {isModalShown && isLoggedIn && (
        <ModalPortal onClose={hideModal} isCloseButtonShown={true}>
          <ProfileModificationForm
            username={profile?.name}
            profileImageUrl={profile?.imageUrl}
            prevDescription={profile?.description}
            onTerminate={hideModal}
          />
        </ModalPortal>
      )}
    </Container>
  );
};

export default ProfileHeader;
