import { Story } from "@storybook/react";

import PortfolioSection, { Props } from "./PortfolioSection";

export default {
  title: "Components/PortfolioSection",
  component: PortfolioSection,
};

const Template: Story<Props> = (args) => <PortfolioSection {...args}>깃들다</PortfolioSection>;

export const Default = Template.bind({});
Default.args = {};
