import { Story } from "@storybook/react";

import PortfolioProjectSection, { Props } from "./PortfolioProjectSection";

export default {
  title: "Components/PortfolioProjectSection",
  component: PortfolioProjectSection,
};

const Template: Story<Props> = (args) => <PortfolioProjectSection {...args}>깃들다</PortfolioProjectSection>;

export const Default = Template.bind({});
Default.args = {};
