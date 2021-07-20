import { Story } from "@storybook/react";

import ProfileFeed, { Props } from "./ProfileFeed";

export default {
  title: "Components/ProfileFeed",
  component: ProfileFeed,
};

const Template: Story<Props> = (args) => <ProfileFeed {...args} />;

export const Default = Template.bind({});
Default.args = {
  isMyFeed: false,
  username: "Chris",
};
