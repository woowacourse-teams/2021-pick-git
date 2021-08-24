import { Story } from "@storybook/react";

import CommentsPage from "./CommentsPage";

export default {
  title: "Pages/CommentsPage",
  component: CommentsPage,
};

const Template: Story = (args) => <CommentsPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
