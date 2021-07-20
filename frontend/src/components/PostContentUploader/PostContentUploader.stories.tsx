import { Story } from "@storybook/react";

import PostContentUploader from "./PostContentUploader";

export default {
  title: "Components/PostContentUploader",
  component: PostContentUploader,
};

const Template: Story = (args) => <PostContentUploader {...args} />;

export const Default = Template.bind({});
Default.args = {};
