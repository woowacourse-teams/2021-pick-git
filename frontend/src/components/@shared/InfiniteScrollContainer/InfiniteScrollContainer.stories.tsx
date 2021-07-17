import { Story } from "@storybook/react";

import InfiniteScrollContainer, { Props } from "./InfiniteScrollContainer";

export default {
  title: "Components/Shared/InfiniteScrollContainer",
  component: InfiniteScrollContainer,
};

const Template: Story<Props> = (args) => <InfiniteScrollContainer {...args}>깃들다</InfiniteScrollContainer>;

export const Default = Template.bind({});
Default.args = {
  onIntersect: () => {
    alert("교차됨!");
  },
};
