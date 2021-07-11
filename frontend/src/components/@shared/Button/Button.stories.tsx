import { Story } from "@storybook/react";

import Button, { Props } from "./Button";

export default {
  title: "Components/Shared/Button",
  component: Button,
};

const Template: Story<Props> = (args) => <Button {...args}>깃들다</Button>;

export const Default = Template.bind({});
Default.args = {};
