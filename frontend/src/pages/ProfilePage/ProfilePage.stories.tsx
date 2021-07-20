import { Story } from "@storybook/react";

import ProfilePage, { Props } from "./ProfilePage";

export default {
  title: "Pages/ProfilePage",
  component: ProfilePage,
};

const Template: Story<Props> = (args) => <ProfilePage {...args} />;

export const MyProfile = Template.bind({});
MyProfile.args = {
  isMyProfile: true,
};

export const UserProfile = Template.bind({});
UserProfile.args = {
  isMyProfile: false,
};
