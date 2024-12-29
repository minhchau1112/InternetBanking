import React from "react";
import { NavLink } from "react-router-dom";
import {
  DashboardOutlined,
  EqualizerOutlined,
  PaymentOutlined,
  PersonOutlineOutlined,
  AccountBalanceOutlined,
  LogoutOutlined,
  NotificationsActiveOutlined,
} from "@mui/icons-material";
import logo from "../logoBank.png";

interface SidebarProps {
  userType: "ROLE_CUSTOMER" | "ROLE_EMPLOYEE" | "ROLE_ADMIN" | null  | undefined;
}

const Sidebar: React.FC<SidebarProps> = ({ userType }) => {
  const getNavLinkClass = ({ isActive }: { isActive: boolean }): string =>
    `flex items-center font-family text-black gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
      isActive
        ? "text-blue-600 font-bold bg-blue-100 rounded-full"
        : "hover:text-blue-600 hover:bg-blue-50 rounded-full"
    }`;

  return (
    <div className="min-h-screen w-64 bg-white shadow-md flex flex-col justify-between">
      <div className="p-6 flex flex-col items-center">
        <img src={logo} alt="Bank Logo" className="h-16 w-16 rounded-full mb-2" />
        <h1 className="text-2xl font-bold text-blue-600">
          Sunrise<span className="text-orange-500">Bank</span>
        </h1>
        <p className="mt-2 text-lg text-gray-500">
          Welcome, <span className="text-lg font-bold">Minh Châu</span>
        </p>
      </div>

      <div className="flex-grow">
        <div className="text-gray-600">
          <NavLink to="/dashboard" className={getNavLinkClass}>
            <DashboardOutlined /> Dashboard
          </NavLink>

          {userType === "ROLE_CUSTOMER" && (
            <>
              <NavLink to="/customer/payments" className={getNavLinkClass}>
                <PaymentOutlined /> Payments
              </NavLink>
              <NavLink to="/customer/transactions" className={getNavLinkClass}>
                <AccountBalanceOutlined /> Transaction
              </NavLink>
              <NavLink to="/customer/debt-reminder" className={getNavLinkClass}>
                <NotificationsActiveOutlined /> Debt Reminder
              </NavLink>
              <NavLink to="/customer/history" className={getNavLinkClass}>
                <EqualizerOutlined /> History
              </NavLink>
              <NavLink to="/customer/profile" className={getNavLinkClass}>
                <PersonOutlineOutlined /> Profile
              </NavLink>
            </>
          )}

          {userType === "ROLE_EMPLOYEE" && (
            <>
              <NavLink to="/employee/customers" className={getNavLinkClass}>
                <PaymentOutlined /> Accounts
              </NavLink>
              <NavLink to="/deposit" className={getNavLinkClass}>
                <AccountBalanceOutlined /> Deposit
              </NavLink>
              <NavLink to="/employee/history" className={getNavLinkClass}>
                <EqualizerOutlined /> History
              </NavLink>
            </>
          )}

          {userType === "ROLE_ADMIN" && (
            <>
              <NavLink to="/admin/employees" className={getNavLinkClass}>
                <PaymentOutlined /> Employees
              </NavLink>
              <NavLink to="/admin/transactions" className={getNavLinkClass}>
                <AccountBalanceOutlined /> Transaction
              </NavLink>
            </>
          )}
        </div>
      </div>

      <div className="px-6 py-4">
        <button className="flex items-center gap-4 text-gray-600 hover:text-blue-600 hover:bg-blue-50 text-md px-4 py-2 rounded-md transition duration-200 ease-in-out">
          <LogoutOutlined /> Log Out
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
