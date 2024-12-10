import React from "react";
import { Routes, Route } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";

function App() {
  const userType = "customer";

  return (
    <div className="flex">
      <Sidebar userType={userType} />
      <div className="flex-grow">
        <Routes>
          <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />
          <Route path="/customer" element={<CustomerDashboard />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
