import { Story } from "@storybook/react";
import BottomSliderPortal, { Props } from "./BottomSliderPortal";

export default {
  title: "Components/BottomSliderPortal",
  component: BottomSliderPortal,
};

const Template: Story<Props> = (args) => <BottomSliderPortal {...args} />;

export const Default = Template.bind({});
Default.args = {};
