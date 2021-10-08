import { Story } from "@storybook/react";

import PageError, { Props } from "./PageError";

export default {
  title: "Components/Shared/PageError",
  component: PageError,
};

const Template: Story<Props> = (args) => <PageError {...args}>깃들다</PageError>;

export const Default = Template.bind({});
Default.args = {};
