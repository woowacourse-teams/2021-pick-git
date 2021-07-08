import { theme } from './App.style';

/// <reference types="node" />
/// <reference types="react" />
/// <reference types="react-dom" />

type StyledTheme = typeof theme;

declare module "*.svg" {
  // const content: React.FunctionComponent<React.SVGAttributes<SVGElement>>;
  const content: string;
  export default content;
}

declare module 'styled-components' {
  export interface DefaultTheme extends StyledTheme {}
}
