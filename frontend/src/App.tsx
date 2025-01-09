import { Routes, Route, useLocation, useNavigate } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
// import useWebSocket from "./hooks/useWebSocket";
import Notifications from "./components/Notification";
// import useStomp from "./hooks/useStomp.ts";
import ViewListDebtReminder from "./pages/customer/ViewListDebtReminder";
import { LoginForm } from "@/pages/login/Login.tsx";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { RootState } from "./redux/store/index.ts";
import DepositPage from "./pages/employee/Deposit";
import Recipient from "./pages/customer/Contact.tsx";
import ForgotPassword from "@/pages/login/ForgotPassword.tsx";
import AccountCreation from "./pages/employee/AccountCreation.tsx";
import TransactionHistory from "./pages/employee/Transaction.tsx";
import TransactionForm from "@/pages/customer/TransactionForm.tsx";
import TransactionHistoryCustomer from "./pages/customer/TransactionHistory.tsx";
import ResetPassword from "@/pages/login/ResetPassword.tsx";
import Profile from "@/pages/customer/Profile.tsx";
import ManageEmployee from "./pages/admin/ManageEmployee.tsx";
import InterbankTransactions from "./pages/admin/InterbankTransactions.tsx";
import TransactionForm from "@/pages/employee/TransactionForm.tsx";

function App() {
    const id = localStorage.getItem('accountId') || "3";
    // const message = useWebSocket("ws://127.0.0.1:8888/ws/notifications", id) || "Thông báo";
    // useStomp("ws://127.0.0.1:8888/ws/notifications", id)
    const id = localStorage.getItem('accountId') || "3";
    // const message = useWebSocket("ws://127.0.0.1:8888/ws/notifications", id) || "Thông báo";
    // useStomp("ws://127.0.0.1:8888/ws/notifications", id)
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
                <div className="absolute top-4 right-4 z-50 bg-white shadow-lg p-4 rounded">
                  <Notifications userId={id} />
                  <Notifications userId={id} />
                </div>
                <Routes>
                    <Route path="/" element={<h1>Welcome to Internet Banking</h1>}/>
                    <Route path="/customer" element={<CustomerDashboard/>}/>
                    <Route path="/debt-reminder" element={<ViewListDebtReminder />} />
                    <Route path="/login" element={<LoginForm/>}/>
                    <Route path="/deposit" element={<DepositPage/>}/>
                    <Route path="/contact" element={<Recipient/>}/>
                    <Route path="/customers" element={< AccountCreation/>}/>
                    <Route path="/history" element={< TransactionHistoryCustomer/>}/>
                    <Route path="/employee-transaction" element={<TransactionHistory/>}/>
                    <Route path="/history" element={< TransactionHistoryCustomer/>}/>
                    <Route path="/employee-transaction" element={<TransactionHistory/>}/>
                    <Route path="/forgot-password" element={<ForgotPassword/>}/>
                    <Route path="/reset-password" element={<ResetPassword/>}/>
                    <Route path="/profile" element={<Profile/>}/>
                    <Route path="/transactions/create" element={<TransactionForm/>}/>
                    <Route path="/manage-employee" element={<ManageEmployee/>}/>
                    <Route path="/interbank-transactions" element={<InterbankTransactions/>}/>
                </Routes>
            </div>
        </div>
    );
}

export default App;
