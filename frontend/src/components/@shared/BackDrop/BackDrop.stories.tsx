import { Story } from "@storybook/react";
import BackDrop, { Props } from "./BackDrop";

export default {
  title: "Components/Layout/BackDrop",
  component: BackDrop,
};

const Template: Story<Props> = (args) => <BackDrop {...args}></BackDrop>;

export const Default = Template.bind({});
(Default as any).args = {
  zIndex: -1,
};
