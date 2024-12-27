import {Routes, Route, useLocation, useNavigate} from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import { LoginForm } from "@/pages/login/Login.tsx";
import {useEffect} from "react";
import {useSelector} from "react-redux";
import { RootState } from "./redux/store/index.ts";

function App() {

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
