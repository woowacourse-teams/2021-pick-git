import { Story } from "@storybook/react";

import PostContentUploader from "./PostContentUploader";

export default {
  title: "Components/PostContentUploader",
  component: PostContentUploader,
};

const Template: Story = (args) => (
  <PostContentUploader
    key="test"
    content="테스트"
    setContent={() => {}}
    setFiles={() => {}}
    isImageUploaderShown={true}
    {...args}
  />
);

export const Default = Template.bind({});
Default.args = {};
