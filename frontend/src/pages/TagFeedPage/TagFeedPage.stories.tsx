import { Story } from "@storybook/react";

import TagFeedPage from "./TagFeedPage";

export default {
  title: "Pages/TagFeedPage",
  component: TagFeedPage,
};

const Template: Story = (args) => <TagFeedPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
