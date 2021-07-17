import { Story } from "@storybook/react";

import PostAddStepHeader from "./PostAddStepHeader";

export default {
  title: "Components/PostAddStepHeader",
  component: PostAddStepHeader,
};

const Template: Story = (args) => <PostAddStepHeader {...args} />;

export const Default = Template.bind({});
Default.args = {};
