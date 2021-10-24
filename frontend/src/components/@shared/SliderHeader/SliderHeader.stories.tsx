import { Story } from "@storybook/react";

import SliderHeader, { Props } from "./SliderHeader";

export default {
  title: "Components/Shared/SliderHeader",
  component: SliderHeader,
};

const Template: Story<Props> = (args) => <SliderHeader {...args} />;

export const Default = Template.bind({});
Default.args = {};
