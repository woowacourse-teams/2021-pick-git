import { useContext } from "react";
import { useQueryClient } from "react-query";
import { Link, useHistory } from "react-router-dom";
import { ThemeContext } from "styled-components";
import { ProfileData } from "../../../@types";
import { WARNING_MESSAGE } from "../../../constants/messages";
import { QUERY } from "../../../constants/queries";
import { PAGE_URL } from "../../../constants/urls";
import UserContext from "../../../contexts/UserContext";
import useMessageModal from "../../../hooks/common/useMessageModal";
import useModal from "../../../hooks/common/useModal";
import useFollow from "../../../hooks/useFollow";
import MessageModalPortal from "../../@layout/MessageModalPortal/MessageModalPortal";
import ModalPortal from "../../@layout/Modal/ModalPortal";
import ProfileModificationForm from "../../ProfileModificationForm/ProfileModificationForm";
import Avatar from "../Avatar/Avatar";
import Button from "../Button/Button";
import CountIndicator from "../CountIndicator/CountIndicator";
import {
  AvatarWrapper,
  ButtonsWrapper,
  Container,
  Indicators,
  IndicatorsWrapper,
  PortfolioButtonCSS,
} from "./ProfileHeader.style";

export interface Props {
  isMyProfile: boolean;
  profile: ProfileData | null;
  username: string;
}

const ProfileHeader = ({ isMyProfile, profile, username }: Props) => {
  const theme = useContext(ThemeContext);
  const { isLoggedIn, currentUsername } = useContext(UserContext);
  const queryClient = useQueryClient();
  const history = useHistory();

  const { isModalShown, showModal, hideModal } = useModal(false);
  const { modalMessage, isModalShown: isMessageModalShown, hideMessageModal, showConfirmModal } = useMessageModal();

  const setProfileQueryData = (following: boolean) => {
    const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, username }];
    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    if (currentProfileQueryData) {
      const { followerCount: currentFollowerCount } = currentProfileQueryData;
      const followerCount = following ? currentFollowerCount + 1 : currentFollowerCount - 1;

      queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
        ...currentProfileQueryData,
        followerCount,
        following,
      });
    }
  };

  const { toggleFollow } = useFollow(setProfileQueryData);

  const toggleFollowWithGithubFollowing = (applyGithub: boolean) => () => {
    if (!profile || profile.following === null) {
      return;
    }

    hideMessageModal();
    toggleFollow(username, profile.following, applyGithub);
  };

  const handleFollowButtonClick = () => {
    if (profile && profile.following !== null) {
      showConfirmModal(profile.following ? WARNING_MESSAGE.GITHUB_UNFOLLOWING : WARNING_MESSAGE.GITHUB_FOLLOWING);
    }
  };

  const handleMoveToPortfolio = () => {
    if (currentUsername === username) {
      history.push(PAGE_URL.MY_PORTFOLIO);
      return;
    }

    history.push({
      pathname: PAGE_URL.PORTFOLIO,
      search: `username=${username}`,
    });
  };

  const ProfileButton = () => {
    if (!isLoggedIn) {
      return <></>;
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
      <AvatarWrapper>
        <Avatar diameter="100%" fontSize="0.875rem" imageUrl={profile?.imageUrl} name={profile?.name} />
      </AvatarWrapper>
      <IndicatorsWrapper>
        <Indicators>
          <CountIndicator name="게시물" count={profile?.postCount ?? 0} />
          <Link to={PAGE_URL.FOLLOWERS(username)}>
            <CountIndicator name="팔로워" count={profile?.followerCount ?? 0} />
          </Link>
          <Link to={PAGE_URL.FOLLOWINGS(username)}>
            <CountIndicator name="팔로잉" count={profile?.followingCount ?? 0} />
          </Link>
        </Indicators>
        <ButtonsWrapper>
          <ProfileButton />
          <Button cssProp={PortfolioButtonCSS} onClick={handleMoveToPortfolio} kind="squaredBlock">
            포트폴리오
          </Button>
        </ButtonsWrapper>
      </IndicatorsWrapper>
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
          confirmText="예"
          cancelText="아니오"
        />
      )}
    </Container>
  );
};

export default ProfileHeader;
