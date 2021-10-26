import { Story } from "@storybook/react";
import { Alert, Props } from "./AlertPortal";

export default {
  title: "Components/Layout/AlertPortal",
  component: Alert,
};

const Template: Story<Props> = (args) => <Alert {...args} />;

export const Default = Template.bind({});
Default.args = {
  heading: "제목",
  onOkay: () => {},
};
