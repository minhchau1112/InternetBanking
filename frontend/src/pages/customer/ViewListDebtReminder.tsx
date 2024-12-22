import { useState } from "react";
import DebtReminderTable from "../../components/DebtReminderTable";
import {
  FormControl,
  Select,
  MenuItem,
  InputLabel,
  SelectChangeEvent,
} from "@mui/material";

const ViewListDebtReminder = () => {
	const [filterCategory, setFilterCategory] = useState("PENDING");

	const handleStatusChange = (e: SelectChangeEvent<string>) => {
		const selectedValue = e.target.value;
		setFilterCategory(selectedValue); 
	};

	return (
		<div className="p-5">
			<h1 className="mb-3 text-center">Debt List</h1>
			<div className="flex justify-end">
			<FormControl size="small" sx={{ minWidth: 120, paddingBottom: 4}}>
				<InputLabel>Category</InputLabel>
				<Select
				value={filterCategory}
				label="Status"
				onChange={handleStatusChange}
				>
				<MenuItem value="PENDING">Pending</MenuItem>
				<MenuItem value="PAID">Paid</MenuItem>
				<MenuItem value="CANCELLED">Cancelled</MenuItem>
				<MenuItem value="ALL">All</MenuItem>
				</Select>
			</FormControl>
			</div>

			<DebtReminderTable status={filterCategory === "ALL" ? "" : filterCategory} />
		</div>
	);
};

export default ViewListDebtReminder;
