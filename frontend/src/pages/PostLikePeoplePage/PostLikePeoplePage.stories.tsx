import { Story } from "@storybook/react";

import PostLikePeoplePage from "./PostLikePeoplePage";

export default {
  title: "Pages/PostLikePeoplePage",
  component: PostLikePeoplePage,
};

const Template: Story = (args) => <PostLikePeoplePage {...args} />;

export const Default = Template.bind({});
Default.args = {};
