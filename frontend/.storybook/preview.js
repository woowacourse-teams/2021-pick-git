import { addDecorator } from '@storybook/react';
import { MemoryRouter } from 'react-router-dom';
import { theme, GlobalStyle } from '../src/App.style';
import { ThemeProvider } from 'styled-components';
import { QueryClientProvider, QueryClient } from 'react-query';

const queryClient = new QueryClient()

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
  },
  layout: 'fullscreen',
}

addDecorator(Story => (
    <MemoryRouter initialEntries={['/']}>
      <ThemeProvider theme={theme}>
        <GlobalStyle />
        <QueryClientProvider client={queryClient}>
          <Story />
        </QueryClientProvider>
      </ThemeProvider>
    </MemoryRouter>
  )
);

localStorage.setItem("accessToken", "test");