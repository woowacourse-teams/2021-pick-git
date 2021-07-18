import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { ProfileData } from "../../../@types";
import UserContext from "../../../contexts/UserContext";
import { useFollowingMutation, useUnfollowingMutation } from "../../../services/queries";
import Avatar from "../Avatar/Avatar";
import Button from "../Button/Button";
import CountIndicator from "../CountIndicator/CountIndicator";
import { Container, Indicators } from "./ProfileHeader.style";

export interface Props {
  profile?: ProfileData;
  isMyProfile: boolean;
}

const ProfileHeader = ({ profile, isMyProfile }: Props) => {
  const { isLoggedIn } = useContext(UserContext);
  const theme = useContext(ThemeContext);

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
      return <div>loading</div>;
    }

    if (isMyProfile) {
      return (
        <Button type="button" kind="squaredBlock" onClick={() => alert("아직 지원하지 않는 기능입니다")}>
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
      <Avatar diameter="3.75rem" fontSize="0.875rem" imageUrl={profile?.image} name={profile?.name} />
      <div>
        <Indicators>
          <CountIndicator name="게시물" count={profile?.postCount ?? 0} />
          <CountIndicator name="팔로워" count={profile?.followerCount ?? 0} />
          <CountIndicator name="팔로잉" count={profile?.followingCount ?? 0} />
        </Indicators>
        <ProfileButton />
      </div>
    </Container>
  );
};

export default ProfileHeader;
