import { Story } from "@storybook/react";
import { ProfileContextProvider } from "../../contexts/ProfileContext";

import ProfilePage from "./ProfilePage";

export default {
  title: "Pages/ProfilePage",
  component: ProfilePage,
};

const Template: Story<{ children: React.ReactNode; isMyProfile: boolean }> = (args) => (
  <ProfileContextProvider {...args}>
    <ProfilePage />
  </ProfileContextProvider>
);

export const MyProfile = Template.bind({});
MyProfile.args = {
  isMyProfile: true,
};

export const UserProfile = Template.bind({});
UserProfile.args = {
  isMyProfile: false,
};
