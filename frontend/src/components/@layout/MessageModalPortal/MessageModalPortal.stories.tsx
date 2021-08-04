import { Story } from "@storybook/react";
import { MessageModal, Props } from "./MessageModalPortal";

export default {
  title: "Components/Layout/MessageModal",
  component: MessageModal,
};

const Template: Story<Props> = (args) => <MessageModal {...args} />;

export const Default = Template.bind({});
Default.args = {
  heading: "제목",
  onClose: () => {},
  onConfirm: () => {},
};
