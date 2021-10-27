import { CSSProp } from "styled-components";
import { Container, Switch, Slider, Checkbox, ToggleButtonText } from "./ToggleButton.style";

export interface Props {
  toggleButtonText?: string;
  isToggled: boolean;
  onToggle: () => void;
  cssProp?: CSSProp;
}

const ToggleButton = ({ toggleButtonText, isToggled, cssProp, onToggle }: Props) => {
  return (
    <Container cssProp={cssProp}>
      <ToggleButtonText>{toggleButtonText}</ToggleButtonText>
      <Switch>
        <Checkbox type="checkbox" checked={isToggled} onChange={onToggle} />
        <Slider />
      </Switch>
    </Container>
  );
};

export default ToggleButton;
