/// <reference types="node" />
/// <reference types="react" />
/// <reference types="react-dom" />

import "styled-components";
import { theme } from "../App.style";

type StyledTheme = typeof theme;

declare module "styled-components" {
  export interface DefaultTheme extends StyledTheme {}
}
