import { Story } from "@storybook/react";

import AddPostPage, { Props } from "./AddPostPage";

export default {
  title: "Pages/AddPostPage",
  component: AddPostPage,
};

const Template: Story<Props> = (args) => <AddPostPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
