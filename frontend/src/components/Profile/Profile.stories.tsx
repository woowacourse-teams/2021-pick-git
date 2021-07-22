import { Story } from "@storybook/react";

import LoggedInWrapper from "../../../.storybook/utils/LoggedInWrapper";
import Profile from "./Profile";

export default {
  title: "Components/Profile",
  component: Profile,
};

const Template: Story = (args) => (
  <LoggedInWrapper>
    <Profile {...args} />
  </LoggedInWrapper>
);

export const Default = Template.bind({});
Default.args = {
  isMyProfile: false,
};

export const ProfileMe = Template.bind({});
ProfileMe.args = {
  isMyProfile: true,
};
