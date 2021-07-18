import { Story } from "@storybook/react";

import LoggedInWrapper from "../../../.storybook/utils/LoggedInWrapper";
import Profile, { Props } from "./Profile";

export default {
  title: "Components/Profile",
  component: Profile,
};

const Template: Story<Props> = (args) => (
  <LoggedInWrapper>
    <Profile {...args} />
  </LoggedInWrapper>
);

export const Default = Template.bind({});
Default.args = {
  isMyProfile: false,
  username: "Chris",
};

export const ProfileMe = Template.bind({});
ProfileMe.args = {
  isMyProfile: true,
  username: "Tanney",
};
