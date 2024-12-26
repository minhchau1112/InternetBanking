import { Routes, Route, useLocation } from "react-router-dom";
import Sidebar from "./components/Sidebar.tsx";
import CustomerDashboard from "./pages/customer/CustomerDashboard.tsx";
// import Login from "./pages/login/Login.tsx";
import {LoginForm} from "@/pages/login/FormShadcn.tsx";

function App() {
    const location = useLocation();

    const isLoginPage = location.pathname === "/login";

    const userType = "customer";

    return (
        <div className={`flex ${isLoginPage ? "items-center justify-center min-h-screen" : ""}`}>
            {!isLoginPage && <Sidebar userType={userType} />}
            {isLoginPage ? (
                <div className="w-full flex justify-center items-center bg-gray-100">
                    <Routes>
                        <Route path="/login" element={<LoginForm />} />
                    </Routes>
                </div>
            ) : (
                <div className="flex-grow">
                    <Routes>
                        <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />
                        <Route path="/customer" element={<CustomerDashboard />} />
                    </Routes>
                </div>
            )}
        </div>
    );
}

export default App;
