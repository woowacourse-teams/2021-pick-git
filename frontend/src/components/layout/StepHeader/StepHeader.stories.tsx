import { Story } from "@storybook/react";

import StepHeader, { Props } from "./StepHeader";

export default {
  title: "Components/Layout/StepHeader",
  component: StepHeader,
};

const Template: Story<Props> = (args) => <StepHeader {...args}>Git 리포지터리</StepHeader>;

export const Default = Template.bind({});
Default.args = {};
