import { Story } from "@storybook/react";
import { Confirm, Props } from "./ConfirmPortal";

export default {
  title: "Components/Layout/Confirm",
  component: Confirm,
};

const Template: Story<Props> = (args) => <Confirm {...args} />;

export const Default = Template.bind({});
Default.args = {
  heading: "제목",
  onConfirm: () => {},
};
