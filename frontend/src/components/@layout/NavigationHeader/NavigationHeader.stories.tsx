import { Story } from "@storybook/react";

import NavigationHeader from "./NavigationHeader";

export default {
  title: "Components/Layout/NavigationHeader",
  component: NavigationHeader,
};

const Template: Story = (args) => <NavigationHeader {...args} />;

export const Default = Template.bind({});
Default.args = {};
