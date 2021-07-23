import { Story } from "@storybook/react";

import LoggedInWrapper from "../../../../.storybook/utils/LoggedInWrapper";
import ProfileHeader, { Props } from "./ProfileHeader";

export default {
  title: "Components/Shared/ProfileHeader",
  component: ProfileHeader,
};

const mockProfile = {
  name: "Beuccol",
  imageUrl:
    "https://images.unsplash.com/photo-1518574095400-c75c9b094daa?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80",
  description: "브로콜리입니다.",
  followingCount: 154,
  followerCount: 13,
  postCount: 102,
  githubUrl: "https://github.com/tanney-102",
  company: "우아한형제들",
  location: "잠실역 9번출구",
  website: "https://techcourse.woowahan.com/",
  twitter: "",
  following: false,
};

const DefaultTemplate: Story<Props> = (args) => <ProfileHeader {...args} />;
const LoggedInTemplate: Story<Props> = (args) => (
  <LoggedInWrapper>
    <ProfileHeader {...args} />
  </LoggedInWrapper>
);

export const Default = DefaultTemplate.bind({});
Default.args = {
  profile: mockProfile,
  isMyProfile: false,
};

export const MyProfile = LoggedInTemplate.bind({});
MyProfile.args = {
  profile: mockProfile,
  isMyProfile: true,
};

export const NotFollowed = LoggedInTemplate.bind({});
NotFollowed.args = {
  profile: mockProfile,
  isMyProfile: false,
};

export const Followed = LoggedInTemplate.bind({});
Followed.args = {
  profile: { ...mockProfile, following: true },
  isMyProfile: false,
};
