import { Story } from "@storybook/react";
import { SearchIcon } from "../../../assets/icons";

import Input, { Props } from "./Input";

export default {
  title: "Components/Shared/Input",
  component: Input,
};

const Template: Story<Props> = (args) => <Input {...args} />;

export const Default = Template.bind({});
Default.args = {};

export const InputWithIcon = Template.bind({});
InputWithIcon.args = {
  icon: <SearchIcon />,
};
