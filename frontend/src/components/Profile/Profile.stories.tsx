import { Story } from "@storybook/react";

import Profile from "./Profile";

export default {
  title: "Components/Profile",
  component: Profile,
};

const Template: Story = (args) => <Profile {...args} />;

export const Default = Template.bind({});
Default.args = {};
