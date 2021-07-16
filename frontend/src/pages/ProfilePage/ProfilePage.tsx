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
  const userName = new URLSearchParams(useLocation().search).get("userName");
  const { currentUserName } = useContext(UserContext);

  if (!isMyProfile && !userName) return <Redirect to={PAGE_URL.HOME} />;

  const tabItems = [
    {
      name: "게시물",
      content: <ProfileFeed isMyFeed={isMyProfile} userName={userName} />,
    },
    {
      name: "활동통계",
      content: <GithubStatistics userName={isMyProfile ? currentUserName : (userName as string)} />,
    },
  ];

  return (
    <Container>
      <Profile isMyProfile={isMyProfile} userName={userName} />
      <Tabs tabItems={tabItems} />
    </Container>
  );
};

export default ProfilePage;
