import { Routes, Route } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import ViewListDebtReminder from "./pages/customer/ViewListDebtReminder";

function App() {
    const userType = "customer";

    return (
        <div className="flex">
            <Sidebar userType={userType} />
            <Routes>
                <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />
                <Route path="/customer" element={<CustomerDashboard />} />
                <Route path="/customer/debt-reminder" element={<ViewListDebtReminder />} />
            </Routes>
        </div>
    );
}

export default App;
