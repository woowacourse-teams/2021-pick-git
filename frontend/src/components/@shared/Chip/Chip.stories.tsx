import { Story } from "@storybook/react";

import Chip, { Props } from "./Chip";

export default {
  title: "Components/Shared/Chip",
  component: Chip,
};

const Template: Story<Props> = (args) => <Chip {...args}>깃들다</Chip>;

export const Default = Template.bind({});
Default.args = {};

export const Deletable = Template.bind({});

Deletable.args = {
  onDelete: () => alert("삭제됨!"),
};
