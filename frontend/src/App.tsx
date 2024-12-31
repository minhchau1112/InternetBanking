import { Routes, Route, useLocation, useNavigate } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import { LoginForm } from "@/pages/login/Login.tsx";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { RootState } from "./redux/store/index.ts";
import DepositPage from "./pages/employee/Deposit";
import Recipient from "./pages/customer/Recipient.tsx";
import ForgotPassword from "@/pages/login/ForgotPassword.tsx";
import AccountCreation from "./pages/employee/AccountCreation.tsx";
import TransactionHistory from "./pages/employee/TransactionHistory.tsx";
import ResetPassword from "@/pages/login/ResetPassword.tsx";
import TransactionForm from "@/pages/employee/TransactionForm.tsx";

function App() {
    const navigate = useNavigate();
    const token = localStorage.getItem("access_token");
    const location = useLocation();
    useEffect(() => {
        if (!token && location.pathname !== "/login" && location.pathname !== "/forgot-password" && location.pathname !== "/reset-password") {
            navigate("/login");
        }
    }, [navigate, token, location.pathname]);

    const user = useSelector((state: RootState) => state.auth.user);

    const userType: "ROLE_CUSTOMER" | "ROLE_EMPLOYEE" | "ROLE_ADMIN" | null =
        user?.role === "ROLE_CUSTOMER" || user?.role === "ROLE_EMPLOYEE" || user?.role === "ROLE_ADMIN"
            ? user?.role
            : null;

    const noSidebarPages = ["/login", "/forgot-password","/reset-password"];

    return (
        <div className="flex w-screen">
            {!noSidebarPages.includes(location.pathname) && <Sidebar userType={userType}/>}
            <div className={`flex-grow`}>
                <Routes>
                    <Route path="/" element={<h1>Welcome to Internet Banking</h1>}/>
                    <Route path="/customer" element={<CustomerDashboard/>}/>
                    <Route path="/login" element={<LoginForm/>}/>
                    <Route path="/deposit" element={<DepositPage/>}/>
                    <Route path="/recipient" element={<Recipient/>}/>
                    <Route path="/customers" element={< AccountCreation/>}/>
                    <Route path="/history" element={< TransactionHistory/>}/>
                    <Route path="/forgot-password" element={<ForgotPassword/>}/>
                    <Route path="/reset-password" element={<ResetPassword/>}/>
                    <Route path="/transactions/createTransaction" element={<TransactionForm/>}/>
                </Routes>
            </div>
        </div>
    );
}

export default App;
