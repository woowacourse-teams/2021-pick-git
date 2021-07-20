import { useContext } from "react";
import { Redirect, useLocation, useParams } from "react-router-dom";

import Tabs from "../../components/@shared/Tabs/Tabs";
import GithubStatistics from "../../components/GithubStatistics/GithubStatistics";
import Profile from "../../components/Profile/Profile";
import ProfileFeed from "../../components/ProfileFeed/ProfileFeed";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import { Container } from "./ProfilePage.style";

export interface Props {
  isMyProfile: boolean;
}

const ProfilePage = ({ isMyProfile }: Props) => {
  const username = new URLSearchParams(useLocation().search).get("username");
  const { currentUsername } = useContext(UserContext);

  if (!isMyProfile && !username) return <Redirect to={PAGE_URL.HOME} />;

  if (username && username === currentUsername) return <Redirect to={PAGE_URL.MY_PROFILE} />;

  const tabItems = [
    {
      name: "게시물",
      content: <ProfileFeed isMyFeed={isMyProfile} username={username} />,
    },
    {
      name: "활동통계",
      content: <GithubStatistics username={isMyProfile ? currentUsername : (username as string)} />,
    },
  ];

  return (
    <Container>
      <Profile isMyProfile={isMyProfile} username={username} />
      <Tabs tabItems={tabItems} />
    </Container>
  );
};

export default ProfilePage;
