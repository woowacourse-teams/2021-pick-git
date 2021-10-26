import { Story } from "@storybook/react";

import Fab, { Props } from "./Fab";

export default {
  title: "Components/Shared/Fab",
  component: Fab,
};

const Template: Story<Props> = (args) => <Fab {...args}>깃들다</Fab>;

export const Default = Template.bind({});
Default.args = {};
