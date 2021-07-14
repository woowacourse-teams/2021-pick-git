import { Story } from "@storybook/react";
import { useContext, useEffect } from "react";
import UserContext, { UserContextProvider } from "../../contexts/UserContext";

import Profile, { Props } from "./Profile";

export default {
  title: "Components/Profile",
  component: Profile,
};

const LoggedInWrapper = ({ children }: { children: React.ReactElement }) => {
  const { login } = useContext(UserContext);

  useEffect(() => login("test", "tanney"), []);

  return <>{children}</>;
};

const Template: Story<Props> = (args) => (
  <UserContextProvider>
    <LoggedInWrapper>
      <Profile {...args} />
    </LoggedInWrapper>
  </UserContextProvider>
);

export const Default = Template.bind({});
Default.args = {
  userName: "Tanney",
};
