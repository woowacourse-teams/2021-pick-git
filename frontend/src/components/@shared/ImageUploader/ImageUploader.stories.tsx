import { Story } from "@storybook/react";

import ImageUploader, { Props } from "./ImageUploader";

export default {
  title: "Components/Shared/ImageUploader",
  component: ImageUploader,
};

const Template: Story<Props> = (args) => <ImageUploader {...args} />;

export const Default = Template.bind({});
Default.args = {
  onFileSave: (files: FileList) => {
    const message = Array.from(files)
      .map((file) => file.name)
      .join(",")
      .concat(" 이미지를 저장했습니다.");
    alert(message);
  },
};
