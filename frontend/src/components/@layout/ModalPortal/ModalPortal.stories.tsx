import { Story } from "@storybook/react";

import { Modal, Props } from "./ModalPortal";

export default {
  title: "Components/Layout/Modal",
  component: Modal,
};

const Template: Story<Props> = (args) => <Modal {...args}>모달입니다</Modal>;

export const Default = Template.bind({});
Default.args = {
  onClose: () => {},
  isCloseButtonShown: true,
};
