import { Story } from "@storybook/react";

import NotFound, { Props } from "./NotFound";

export default {
  title: "Components/Shared/NotFound",
  component: NotFound,
};

const Template: Story<Props> = (args) => <NotFound {...args}>깃들다</NotFound>;

export const Default = Template.bind({});
Default.args = {};
