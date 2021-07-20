import { Story } from "@storybook/react";

import CountIndicator, { Props } from "./CountIndicator";

export default {
  title: "Components/Shared/CountIndicator",
  component: CountIndicator,
};

const Template: Story<Props> = (args) => <CountIndicator {...args} />;

export const Default = Template.bind({});

Default.args = {
  name: "게시물",
  count: 102,
};
