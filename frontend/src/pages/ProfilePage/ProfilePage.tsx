import { useContext, useState } from "react";
import { Redirect, useLocation } from "react-router-dom";
import { TabItem } from "../../@types";

import Tabs from "../../components/@shared/Tabs/Tabs";
import Profile from "../../components/Profile/Profile";
import ProfileTabContents from "../../components/ProfileTabContents/ProfileTabContents";
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

  const fixedUsername = isMyProfile ? currentUsername : username;
  const tabItems: TabItem[] = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));

  if (!fixedUsername) return <Redirect to={PAGE_URL.HOME} />;

  return (
    <Container>
      <Profile isMyProfile={isMyProfile} username={fixedUsername} />
      <Tabs tabIndicatorKind="line" tabItems={tabItems} />
      <ProfileTabContents isMyProfile={isMyProfile} username={fixedUsername} tabIndex={tabIndex} />
    </Container>
  );
};

export default ProfilePage;
