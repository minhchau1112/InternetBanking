import {Routes, Route, useLocation, useNavigate} from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import useWebSocket from "./hooks/useWebSocket";
import Notifications from "./components/Notification";
import ViewListDebtReminder from "./pages/customer/ViewListDebtReminder";
import { LoginForm } from "@/pages/login/Login.tsx";
import {useEffect} from "react";
import {useSelector} from "react-redux";
import { RootState } from "./redux/store/index.ts";
import DepositPage from "./pages/employee/Deposit";
import Recipient from "./pages/customer/Recipient.tsx";
import AccountCreation from "./pages/employee/AccountCreation.tsx";
import TransactionHistory from "./pages/employee/TransactionHistory.tsx";

function App() {
    const messages = useWebSocket("ws://localhost:8888/ws/notifications?account=4");
    const navigate = useNavigate();
    const token = localStorage.getItem("access_token");
    useEffect(() => {
        if (!token) {
            navigate("/login");
        }
    }, [navigate, token]);
    const user = useSelector((state: RootState) => state.auth.user);

    const location = useLocation();
    const isLoginPage = location.pathname === "/login";

    const userType: "ROLE_CUSTOMER" | "ROLE_EMPLOYEE" | "ROLE_ADMIN" | null =
        user?.role === "ROLE_CUSTOMER" || user?.role === "ROLE_EMPLOYEE" || user?.role === "ROLE_ADMIN"
            ? user?.role
            : null;

    return (
        <div className="flex w-screen">
            {!isLoginPage && <Sidebar userType={userType}/>}
            <div className={`flex-grow ${isLoginPage ? "" : "ml-64"}`}>
                <div className="absolute top-4 right-4 z-50 bg-white shadow-lg p-4 rounded">
                  <Notifications messages={messages} />
                </div>

                <Routes>
                    <Route path="/" element={<h1>Welcome to Internet Banking</h1>}/>
                    <Route path="/customer" element={<CustomerDashboard/>}/>
                    <Route path="/debt-reminder" element={<ViewListDebtReminder />} />
                    <Route path="/login" element={<LoginForm/>}/>
                    <Route path="/deposit" element={<DepositPage/>}/>
                    <Route path="/recipient" element={<Recipient/>}/>
                    <Route path="/customers" element={< AccountCreation/>}/>
                    <Route path="/history" element={< TransactionHistory/>}/>
                </Routes>
            </div>
        </div>
    );
}

export default App;
