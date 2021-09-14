import { Story } from "@storybook/react";

import ToggleButton, { Props } from "./ToggleButton";

export default {
  title: "Components/Shared/ToggleButton",
  component: ToggleButton,
};

const Template: Story<Props> = (args) => <ToggleButton {...args}>깃들다</ToggleButton>;

export const Default = Template.bind({});
Default.args = {};
