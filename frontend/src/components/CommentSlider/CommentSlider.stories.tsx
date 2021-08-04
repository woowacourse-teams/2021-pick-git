import { Story } from "@storybook/react";
import CommentSlider, { Props } from "./CommentSlider";

export default {
  title: "Components/CommentSlider",
  component: CommentSlider,
};

const Template: Story<Props> = (args) => <CommentSlider {...args} />;

export const Default = Template.bind({});
Default.args = {};
