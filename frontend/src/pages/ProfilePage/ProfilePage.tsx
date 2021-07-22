import { useContext, useState } from "react";
import { Redirect } from "react-router-dom";
import { TabItem } from "../../@types";

import Tabs from "../../components/@shared/Tabs/Tabs";
import GithubStatistics from "../../components/GithubStatistics/GithubStatistics";
import Profile from "../../components/Profile/Profile";
import ProfileFeed from "../../components/ProfileFeed/ProfileFeed";
import { PAGE_URL } from "../../constants/urls";
import ProfileContext from "../../contexts/ProfileContext";
import { Container } from "./ProfilePage.style";

const tabNames = ["게시물", "활동통계"];

const ProfilePage = () => {
  const [tabIndex, setTabIndex] = useState(0);
  const { isMyProfile, username } = useContext(ProfileContext) ?? {};

  const tabItems: TabItem[] = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [<ProfileFeed key="profile-feed" />, <GithubStatistics key="github-stats" />];

  if (!isMyProfile && !username) return <Redirect to={PAGE_URL.HOME} />;

  return (
    <Container>
      <Profile />
      <Tabs tabItems={tabItems} />
      {tabContents[tabIndex]}
    </Container>
  );
};

export default ProfilePage;
