import { Story } from "@storybook/react";

import CircleIcon, { Props } from "./CircleIcon";

export default {
  title: "Components/Shared/CircleIcon",
  component: CircleIcon,
};

const Template: Story<Props> = (args) => <CircleIcon {...args}>Git</CircleIcon>;

export const Default = Template.bind({});
Default.args = {
  diameter: "3rem",
  name: "깃들다 들다",
};
