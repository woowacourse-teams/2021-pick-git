import { Story } from "@storybook/react";

import UserFeedPage from "./UserFeedPage";

export default {
  title: "Pages/UserFeedPage",
  component: UserFeedPage,
};

const Template: Story = (args) => <UserFeedPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
