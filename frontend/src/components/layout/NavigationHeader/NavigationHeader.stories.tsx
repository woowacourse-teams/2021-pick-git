import { Story } from "@storybook/react";

import NavigationHeader, { Props } from "./NavigationHeader";

export default {
  title: "Components/Layout/NavigationHeader",
  component: NavigationHeader,
};

const Template: Story<Props> = (args) => <NavigationHeader {...args} />;

export const Default = Template.bind({});
Default.args = {};
