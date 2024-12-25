import { Routes, Route } from "react-router-dom";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Sidebar from "./components/Sidebar";
import useWebSocket from "./hooks/useWebSocket";
import Notifications from "./components/Notification";
import ViewListDebtReminder from "./pages/customer/ViewListDebtReminder";

function App() {
  const userType = "customer";
  const messages = useWebSocket("ws://localhost:8888/ws/notifications?account=4");

  return (
    <div className="flex">
      <Sidebar userType={userType} />
      <div className="flex-grow relative">
        <div className="absolute top-4 right-4 z-50 bg-white shadow-lg p-4 rounded">
          <Notifications messages={messages} />
        </div>

        <Routes>
          <Route path="/" element={<h1>Welcome to Internet Banking</h1>} />
          <Route path="/customer" element={<CustomerDashboard />} />
          <Route path="/customer/debt-reminder" element={<ViewListDebtReminder />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
