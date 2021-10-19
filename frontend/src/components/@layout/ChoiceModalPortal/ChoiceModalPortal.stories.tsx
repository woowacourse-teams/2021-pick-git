import { Story } from "@storybook/react";
import { ChoiceModal, Props } from "./ChoiceModalPortal";

export default {
  title: "Components/Layout/Confirm",
  component: ChoiceModal,
};

const Template: Story<Props> = (args) => <ChoiceModal {...args} />;

export const Default = Template.bind({});
Default.args = {
  heading: "제목",
  onClose: () => {},
};
