import { Story } from "@storybook/react";

import { TrashIcon, EditIcon } from "../../../assets/icons";
import ButtonDrawer, { Props } from "./ButtonDrawer";

export default {
  title: "Components/Shared/ButtonDrawer",
  component: ButtonDrawer,
};

const Template: Story<Props> = (args) => (
  <div style={{ margin: "50px" }}>
    <ButtonDrawer {...args} />
  </div>
);

export const Default = Template.bind({});
Default.args = {
  circleButtons: [
    { node: <EditIcon />, onClick: () => alert("click!") },
    { node: <TrashIcon />, onClick: () => alert("click!") },
  ],
};
