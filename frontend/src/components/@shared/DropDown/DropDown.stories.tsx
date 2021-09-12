import { Story } from "@storybook/react";

import DropDown, { Props } from "./DropDown";

export default {
  title: "Components/Shared/DropDown",
  component: DropDown,
};

const Template: Story<Props> = (args) => <DropDown {...args}>깃들다</DropDown>;

export const Default = Template.bind({});
Default.args = {};
