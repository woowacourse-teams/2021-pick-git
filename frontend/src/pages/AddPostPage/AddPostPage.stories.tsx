import { Story } from "@storybook/react";

import AddPostPage from "./AddPostPage";

export default {
  title: "Pages/AddPostPage",
  component: AddPostPage,
};

const Template: Story = (args) => <AddPostPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
