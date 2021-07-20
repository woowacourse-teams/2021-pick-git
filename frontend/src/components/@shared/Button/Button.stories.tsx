import { Story } from "@storybook/react";

import Button, { Props } from "./Button";

export default {
  title: "Components/Shared/Button",
  component: Button,
};

const Template: Story<Props> = (args) => <Button {...args}>깃들다</Button>;

export const Default = Template.bind({});
Default.args = {};

export const BaemintSquaredInline = Template.bind({});
BaemintSquaredInline.args = {
  kind: "squaredInline",
  backgroundColor: "#40b7b2",
};

export const BaemintSquaredBlock = Template.bind({});
BaemintSquaredBlock.args = {
  kind: "squaredBlock",
  backgroundColor: "#40b7b2",
};

export const BaemintRoundedInline = Template.bind({});
BaemintRoundedInline.args = {
  kind: "roundedInline",
  backgroundColor: "#40b7b2",
};

export const BaemintRoundedBlock = Template.bind({});
BaemintRoundedBlock.args = {
  kind: "roundedBlock",
  backgroundColor: "#40b7b2",
};
