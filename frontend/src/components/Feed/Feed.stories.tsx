import { Story } from "@storybook/react";

import Feed from "./Feed";

export default {
  title: "Components/Feed",
  component: Feed,
};

const Template: Story = (args) => <Feed {...args} />;

export const Default = Template.bind({});
Default.args = {};
