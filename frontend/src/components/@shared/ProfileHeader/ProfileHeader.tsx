import { useContext } from "react";
import { ThemeContext } from "styled-components";
import { ProfileData } from "../../../@types";
import { WARNING_MESSAGE } from "../../../constants/messages";

import UserContext from "../../../contexts/UserContext";
import useMessageModal from "../../../services/hooks/@common/useMessageModal";
import useModal from "../../../services/hooks/@common/useModal";
import useFollow from "../../../services/hooks/useFollow";
import MessageModalPortal from "../../@layout/MessageModalPortal/MessageModalPortal";
import ModalPortal from "../../@layout/Modal/ModalPortal";
import ProfileModificationForm from "../../ProfileModificationForm/ProfileModificationForm";
import Avatar from "../Avatar/Avatar";
import Button from "../Button/Button";
import CountIndicator from "../CountIndicator/CountIndicator";
import { ButtonLoader, ButtonSpinner, Container, Indicators } from "./ProfileHeader.style";

export interface Props {
  isMyProfile: boolean;
  profile: ProfileData | null;
  username: string;
}

const ProfileHeader = ({ isMyProfile, profile, username }: Props) => {
  const theme = useContext(ThemeContext);
  const { isLoggedIn } = useContext(UserContext);
  const { isModalShown, showModal, hideModal } = useModal(false);
  const { modalMessage, isModalShown: isMessageModalShown, hideMessageModal, showConfirmModal } = useMessageModal();
  const { toggleFollow, isFollowLoading, isUnfollowLoading } = useFollow();

  const toggleFollowWithGithubFollowing = (applyGithub: boolean) => () => {
    if (profile && profile.following !== null) {
      toggleFollow(username, profile.following, applyGithub);
    }

    hideMessageModal();
  };

  const handleFollowButtonClick = () => {
    if (profile && profile.following !== null) {
      showConfirmModal(profile.following ? WARNING_MESSAGE.GITHUB_UNFOLLOWING : WARNING_MESSAGE.GITHUB_FOLLOWING);
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
          onClick={handleFollowButtonClick}
        >
          팔로우 취소
        </Button>
      );
    } else {
      return (
        <Button type="button" kind="squaredBlock" onClick={handleFollowButtonClick}>
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
            username={username}
            profileImageUrl={profile?.imageUrl}
            prevDescription={profile?.description}
            onTerminate={hideModal}
          />
        </ModalPortal>
      )}
      {isMessageModalShown && (
        <MessageModalPortal
          heading={modalMessage}
          onConfirm={toggleFollowWithGithubFollowing(true)}
          onCancel={toggleFollowWithGithubFollowing(false)}
          onClose={hideMessageModal}
        />
      )}
    </Container>
  );
};

export default ProfileHeader;
