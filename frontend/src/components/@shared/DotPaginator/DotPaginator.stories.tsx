import { Story } from "@storybook/react";

import DotPaginator, { Props } from "./DotPaginator";

export default {
  title: "Components/Shared/DotPaginator",
  component: DotPaginator,
};

const Template: Story<Props> = (args) => <DotPaginator {...args}>깃들다</DotPaginator>;

export const Default = Template.bind({});

Default.args = {};
