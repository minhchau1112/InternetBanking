import React from "react";
import { NavLink } from "react-router-dom";
import {
  DashboardOutlined,
  EqualizerOutlined,
  PaymentOutlined,
  PersonOutlineOutlined,
  AccountBalanceOutlined,
  LogoutOutlined,
  NotificationsActiveOutlined
} from "@mui/icons-material";
import logo from "../logoBank.png";

const Sidebar = ({ userType }) => {
  return (
    <div className="min-h-screen w-64 bg-white shadow-md flex flex-col justify-between">
		<div className="p-6 flex flex-col items-center">
			<img src={logo} alt="Bank Logo" className="h-16 w-16 rounded-full mb-2" />
			<h1 className="text-2xl font-bold text-blue-600">
			Sunrise<span className="text-orange-500">Bank</span>
			</h1>
			<p className="mt-2 text-lg text-gray-500">Welcome, <span className="text-lg font-bold">Minh Châu</span></p>
		</div>

		<div className="flex-grow">
			<div className="text-gray-600">
				<NavLink
					to="/dashboard"
					className={({ isActive }) =>
					`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
						isActive
						? "text-blue-600 font-bold bg-blue-100 rounded-full"
						: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
					}`
					}
				>
					<DashboardOutlined /> Dashboard
				</NavLink>

				{userType === "customer" && (
					<>
					
					<NavLink
					to="/payments"
					className={({ isActive }) =>
					`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
						isActive
						? "text-blue-600 font-bold bg-blue-100 rounded-full"
						: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
					}`
					}
				>
						<PaymentOutlined /> Payments
					</NavLink>

					<NavLink
						to="/transactions"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
						<AccountBalanceOutlined /> Transaction
					</NavLink>

					<NavLink
						to="/debt-reminder"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
						<NotificationsActiveOutlined /> Debt Reminder
					</NavLink>

					<NavLink
						to="/history"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
						<EqualizerOutlined /> History
					</NavLink>

					<NavLink
						to="/profile"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
						<PersonOutlineOutlined /> Profile
					</NavLink>

					</>
				)}

				{userType === "employee" && (
					<>
					
					<NavLink
					to="/customers"
					className={({ isActive }) =>
					`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
						isActive
						? "text-blue-600 font-bold bg-blue-100 rounded-full"
						: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
					}`
					}
				>
						<PaymentOutlined /> Accounts
					</NavLink>

					<NavLink
						to="/transactions"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
						<AccountBalanceOutlined /> Transaction
					</NavLink>

					<NavLink
						to="/history"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
						<EqualizerOutlined /> History
					</NavLink>
					</>
				)}

				{userType === "admin" && (
					<>
					
					<NavLink
					to="/employees"
					className={({ isActive }) =>
					`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
						isActive
						? "text-blue-600 font-bold bg-blue-100 rounded-full"
						: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
					}`
					}
				>
						<PaymentOutlined /> Employees
					</NavLink>

					<NavLink
						to="/transactions"
						className={({ isActive }) =>
						`flex items-center gap-4 px-6 py-3 text-lg transition duration-200 ease-in-out ${
							isActive
							? "text-blue-600 font-bold bg-blue-100 rounded-full"
							: "hover:text-blue-600 hover:bg-blue-50 rounded-full"
						}`
						}
					>
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
