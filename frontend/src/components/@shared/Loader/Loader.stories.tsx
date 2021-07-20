import { Story } from "@storybook/react";

import Loader, { Props } from "./Loader";

export default {
  title: "Components/Shared/Loader",
  component: Loader,
};

const Template: Story<Props> = (args) => <Loader {...args}>깃들다</Loader>;

export const Dots = Template.bind({});

export const Spinner = Template.bind({});

Dots.args = {
  kind: "dots",
  size: "1rem",
};

Spinner.args = {
  kind: "spinner",
  size: "1.6rem",
};
