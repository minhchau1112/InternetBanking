import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard.js";

function App() {
  return (
    <Router>
      <Routes>
        {/* Trang chính */}
        <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />

        {/* Trang customer */}
        <Route path="/customer" element={<CustomerDashboard />} />
      </Routes>
    </Router>
  );
}

export default App;
