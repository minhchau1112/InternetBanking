import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import { BrowserRouter as Router } from "react-router-dom";  
import { SnackbarProvider } from 'notistack';
import { Provider } from "react-redux";
import { store } from "@/redux/store";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>  {/* Wrap the app with Provider */}
      <Router>
        <SnackbarProvider
          maxSnack={3} 
          anchorOrigin={{
            vertical: 'top',
            horizontal: 'right',
          }}
          autoHideDuration={3000}
        >
          <App />
        </SnackbarProvider>
      </Router>
    </Provider>
  </StrictMode>
);