import { Story } from "@storybook/react";

import ScrollActiveHeader, { Props } from "./ScrollActiveHeader";

export default {
  title: "Components/Shared/ScrollActiveHeader",
  component: ScrollActiveHeader,
};

const Template: Story<Props> = (args) => <ScrollActiveHeader {...args}>깃들다</ScrollActiveHeader>;

export const Default = Template.bind({});
Default.args = {};
