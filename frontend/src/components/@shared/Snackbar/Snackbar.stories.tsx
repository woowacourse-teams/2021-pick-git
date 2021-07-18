import { Story, Meta } from "@storybook/react";

import SnackBar, { Props } from "./Snackbar";

export default {
  title: "Components/Shared/SnackBar",
  component: SnackBar,
} as Meta;

const Template: Story<Props> = (args) => <SnackBar {...args} />;

export const Order1 = Template.bind({});
export const Order2 = Template.bind({});

Order1.args = {
  children: "스낵바입니다.",
  order: 1,
};

Order2.args = {
  children: "스낵바입니다.",
  order: 2,
};
