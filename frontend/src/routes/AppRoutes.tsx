import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import CustomerDashboard from "../pages/customer/CustomerDashboard";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />

        <Route path="/customer" element={<CustomerDashboard />} />
      </Routes>
    </Router>
  );
}

export default App;