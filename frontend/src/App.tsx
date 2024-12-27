import { Routes, Route, useLocation } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import { LoginForm } from "@/pages/login/Login.tsx";

function App() {
    const location = useLocation();
    const isLoginPage = location.pathname === "/login";
    const userType = "employee";

    return (
        <div className="flex">
            {!isLoginPage && <Sidebar userType={userType} />}
            <div className={`flex-grow ${isLoginPage ? "" : "ml-64"}`}>
                <Routes>
                    <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />
                    <Route path="/customer" element={<CustomerDashboard />} />
                    <Route path="/login" element={<LoginForm />} />
                </Routes>
            </div>
        </div>
    );
}

export default App;
