import { addDecorator } from '@storybook/react';
import { MemoryRouter } from 'react-router-dom';
import { theme, GlobalStyle } from '../src/App.style';
import { ThemeProvider } from 'styled-components';

export const parameters = {
  actions: { argTypesRegex: "^on[A-Z].*" },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
  viewport: {
    viewports: {
      mobile: {
        name: "mobile",
        styles: {
          width: "375px",
          height: "812px",
        },
      }
    }
  }
}

addDecorator(Story => (
    <MemoryRouter initialEntries={['/']}>
      <ThemeProvider theme={theme}>
        <GlobalStyle />
        <Story />
      </ThemeProvider>
    </MemoryRouter>
  )
)