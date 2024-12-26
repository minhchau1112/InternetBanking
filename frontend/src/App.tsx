import { Routes, Route } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import AccountCreation from "./pages/employee/AccountCreation";

function App() {
  const userType = "employee";

  return (
    <div className="flex">
      <Sidebar userType={userType} />
      <div className="flex-grow">
        <Routes>
          <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />
          <Route path="/customer" element={<CustomerDashboard />} />
          <Route path="/customers" element={<AccountCreation/>} />
        </Routes>
      </div>
    </div>
  );
}

export default App;