import { useContext, useState } from "react";
import { Redirect, useLocation } from "react-router-dom";
import { TabItem } from "../../@types";

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

const tabNames = ["게시물", "활동통계"];

const ProfilePage = ({ isMyProfile }: Props) => {
  const username = new URLSearchParams(useLocation().search).get("username");
  const { currentUsername } = useContext(UserContext);
  const [tabIndex, setTabIndex] = useState(0);

  const tabItems: TabItem[] = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [
    <ProfileFeed key="profile-feed" isMyFeed={isMyProfile} username={username} />,
    <GithubStatistics key="github-stats" username={isMyProfile ? currentUsername : username ?? ""} />,
  ];

  if (!isMyProfile && !username) return <Redirect to={PAGE_URL.HOME} />;

  if (username && username === currentUsername) return <Redirect to={PAGE_URL.MY_PROFILE} />;

  return (
    <Container>
      <Profile isMyProfile={isMyProfile} username={username} />
      <Tabs tabItems={tabItems} />
      {tabContents[tabIndex]}
    </Container>
  );
};

export default ProfilePage;
