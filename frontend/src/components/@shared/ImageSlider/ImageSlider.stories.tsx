import { Story } from "@storybook/react";

import ImageSlider, { Props } from "./ImageSlider";

export default {
  title: "Components/Shared/ImageSlider",
  component: ImageSlider,
};

const Template: Story<Props> = (args) => <ImageSlider {...args} />;

export const inBox = Template.bind({});
inBox.args = {
  imageUrls: [
    "https://images.unsplash.com/photo-1625776730059-488c148cf868?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1825&q=80",
    "https://images.unsplash.com/photo-1625777719145-b3a5ff913418?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80",
    "https://images.unsplash.com/photo-1625851740514-6ce39269bc40?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1275&q=80",
  ],
  slideButtonKind: "in-box",
  width: "100%",
};
