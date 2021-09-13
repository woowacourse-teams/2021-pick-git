import { Story } from "@storybook/react";

import PostSelector, { Props } from "./PostSelector";

export default {
  title: "Components/PostSelector",
  component: PostSelector,
};

const Template: Story<Props> = (args) => <PostSelector {...args} />;

export const Default = Template.bind({});
Default.args = {};
