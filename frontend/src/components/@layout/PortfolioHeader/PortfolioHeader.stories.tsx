import { Story } from "@storybook/react";

import PortfolioHeader, { Props } from "./PortfolioHeader";

export default {
  title: "Components/Shared/PortfolioHeader",
  component: PortfolioHeader,
};

const Template: Story<Props> = (args) => <PortfolioHeader {...args}>깃들다</PortfolioHeader>;

export const Default = Template.bind({});
Default.args = {};
