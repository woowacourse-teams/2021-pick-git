import { Story } from "@storybook/react";

import DateInput, { Props } from "./DateInput";

export default {
  title: "Components/Shared/DateInput",
  component: DateInput,
};

const Template: Story<Props> = (args) => <DateInput {...args}>깃들다</DateInput>;

export const Default = Template.bind({});
Default.args = {};
