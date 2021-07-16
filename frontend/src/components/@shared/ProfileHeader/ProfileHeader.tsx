import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { Profile } from "../../../@types";
import UserContext from "../../../contexts/UserContext";
import Avatar from "../Avatar/Avatar";
import Button from "../Button/Button";
import CountIndicator from "../CountIndicator/CountIndicator";
import { Container, Indicators } from "./ProfileHeader.style";

export interface Props {
  profile?: Profile;
  isMyProfile: boolean;
}

const ProfileHeader = ({ profile, isMyProfile }: Props) => {
  const { isLoggedIn } = useContext(UserContext);
  const theme = useContext(ThemeContext);

  const ProfileButton = () => {
    if (!isLoggedIn) {
      return <></>;
    }

    if (isMyProfile) {
      return (
        <Button type="button" kind="squaredBlock">
          프로필 수정
        </Button>
      );
    }

    if (profile?.following) {
      return (
        <Button type="button" kind="squaredBlock" backgroundColor={theme.color.tertiaryColor}>
          팔로우 취소
        </Button>
      );
    } else {
      return (
        <Button type="button" kind="squaredBlock">
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
