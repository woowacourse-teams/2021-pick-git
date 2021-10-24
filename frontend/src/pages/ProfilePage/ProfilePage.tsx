import { Suspense, useState } from "react";
import { Redirect, useLocation } from "react-router-dom";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import Tabs from "../../components/@shared/Tabs/Tabs";
import Profile from "../../components/Profile/Profile";
import ProfileTabContents from "../../components/ProfileTabContents/ProfileTabContents";

import { PAGE_URL } from "../../constants/urls";

import useAuth from "../../hooks/common/useAuth";

import { Container, LoadingWrapper } from "./ProfilePage.style";

import type { TabItem } from "../../@types";
import { ScrollPageWrapper } from "../../components/@styled/layout";

export interface Props {
  isMyProfile: boolean;
}

const tabNames = ["게시물", "활동통계"];

const ProfilePage = ({ isMyProfile }: Props) => {
  const [tabIndex, setTabIndex] = useState(0);
  const username = new URLSearchParams(useLocation().search).get("username");
  const { currentUsername } = useAuth();

  const fixedUsername = isMyProfile ? currentUsername : username;
  const tabItems: TabItem[] = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));

  if (currentUsername === username) return <Redirect to={PAGE_URL.MY_PROFILE} />;

  if (!fixedUsername) return <Redirect to={PAGE_URL.HOME} />;

  return (
    <Suspense
      fallback={
        <LoadingWrapper>
          <PageLoadingWithLogo />
        </LoadingWrapper>
      }
    >
      <ScrollPageWrapper>
        <Container>
          <Profile isMyProfile={isMyProfile} username={fixedUsername} />
          <Tabs tabIndicatorKind="line" tabItems={tabItems} />
          <ProfileTabContents isMyProfile={isMyProfile} username={fixedUsername} tabIndex={tabIndex} />
        </Container>
      </ScrollPageWrapper>
    </Suspense>
  );
};

export default ProfilePage;
