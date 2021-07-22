import { useContext } from "react";
import { ThemeContext } from "styled-components";

import ProfileContext from "../../../contexts/ProfileContext";
import SnackBarContext from "../../../contexts/SnackbarContext";
import UserContext from "../../../contexts/UserContext";
import { useFollowingMutation, useUnfollowingMutation } from "../../../services/queries";
import Avatar from "../Avatar/Avatar";
import Button from "../Button/Button";
import CountIndicator from "../CountIndicator/CountIndicator";
import { ButtonLoader, ButtonSpinner, Container, Indicators } from "./ProfileHeader.style";

const ProfileHeader = () => {
  const theme = useContext(ThemeContext);
  const { isLoggedIn } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { isMyProfile, profileProps } = useContext(ProfileContext) ?? {};

  const { data } = profileProps ?? {};

  const { mutate: follow, isLoading: isFollowLoading } = useFollowingMutation(data?.name);
  const { mutate: unFollow, isLoading: isUnfollowLoading } = useUnfollowingMutation(data?.name);

  const onFollowButtonClick = () => {
    if (data?.following === null) return;

    if (data?.following) {
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
        <Button type="button" kind="squaredBlock" onClick={() => pushSnackbarMessage("아직 지원하지 않는 기능입니다")}>
          프로필 수정
        </Button>
      );
    }

    if (data?.following) {
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
      <Avatar diameter="3.75rem" fontSize="0.875rem" imageUrl={data?.imageUrl} name={data?.name} />
      <div>
        <Indicators>
          <CountIndicator name="게시물" count={data?.postCount ?? 0} />
          <CountIndicator name="팔로워" count={data?.followerCount ?? 0} />
          <CountIndicator name="팔로잉" count={data?.followingCount ?? 0} />
        </Indicators>
        <ProfileButton />
      </div>
    </Container>
  );
};

export default ProfileHeader;
