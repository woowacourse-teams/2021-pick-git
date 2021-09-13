import { Story } from "@storybook/react";
import { ChangeEventHandler, useState } from "react";

import PortfolioTextEditor, { Props } from "./PortfolioTextEditor";
import { TextEditorWrapper } from "../../../.storybook/utils/components";

type ContainerProps = Omit<Props, "value" | "onChange">;

const Container = (args: ContainerProps) => {
  const [value, setValue] = useState("");
  const onChange: ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => setValue(value);

  return (
    <TextEditorWrapper>
      <PortfolioTextEditor value={value} onChange={onChange} {...args} />
    </TextEditorWrapper>
  );
};

const TransparentContainer = (args: ContainerProps) => {
  const [value, setValue] = useState("");
  const onChange: ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => setValue(value);

  return <PortfolioTextEditor value={value} onChange={onChange} {...args} />;
};

export default {
  title: "Components/Shared/PortfolioTextEditor",
  component: Container,
};

const Template: Story<ContainerProps> = (args) => <Container {...args} />;

export const Default = Template.bind({});
Default.args = {};
