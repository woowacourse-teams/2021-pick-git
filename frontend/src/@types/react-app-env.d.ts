/// <reference types="node" />
/// <reference types="react" />
/// <reference types="react-dom" />

import "styled-components";
import { theme } from "../App.style";
import { CSSProp } from "styled-components";

type StyledTheme = typeof theme;

declare module "styled-components" {
  export interface DefaultTheme extends StyledTheme {}
}

declare interface ObjectConstructor {
  keys<T>(obj: T): Array<keyof T>;
}

