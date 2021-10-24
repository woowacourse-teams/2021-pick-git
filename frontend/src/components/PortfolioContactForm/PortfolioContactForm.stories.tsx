import { Story } from "@storybook/react";

import PortfolioContactForm, { Props } from "./PortfolioContactForm";

export default {
  title: "Components/PortfolioContactForm",
  component: PortfolioContactForm,
};

const Template: Story<Props> = (args) => <PortfolioContactForm {...args}>깃들다</PortfolioContactForm>;

export const Default = Template.bind({});
Default.args = {};
