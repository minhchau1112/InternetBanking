// import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { BrowserRouter as Router } from "react-router-dom";  
import { SnackbarProvider } from 'notistack';

createRoot(document.getElementById('root')!).render(
  // <StrictMode>
  //   <App />
  // </StrictMode>,
  <Router>
    <SnackbarProvider
			maxSnack={3} // Tối đa 3 thông báo cùng lúc
			anchorOrigin={{
				vertical: 'top', // Vị trí trên cùng
				horizontal: 'right', // Góc phải
			}}
			autoHideDuration={3000} // Tự động ẩn sau 3 giây
		>
			<App />
		</SnackbarProvider>
  </Router>
)
