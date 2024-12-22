import { useState } from "react";
import DebtReminderTable from "../../components/DebtReminderTable";
import {
  FormControl,
  Select,
  MenuItem,
  InputLabel,
  SelectChangeEvent,
  Box,
  Tabs,
  Tab,
} from "@mui/material";

const ViewListDebtReminder = () => {
	const [tab, setTab] = useState(0);

	const [filter, setFilter] = useState("PENDING");

	const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
		setTab(newValue);
	};
	
	const handleStatusChange = (e: SelectChangeEvent<string>) => {
		const selectedValue = e.target.value;
		setFilter(selectedValue); 
	};

	const currentType = tab === 0 ? "Creator" : "Debtor";

	return (
		<Box sx={{paddingLeft: 4, paddingTop: 4}}>
			<Tabs value={tab} onChange={handleTabChange}>
				<Tab label="Created by You" />
				<Tab label="Received from Others" />
			</Tabs>

			<Box className="flex justify-end">
				<FormControl size="small" sx={{ minWidth: 120, paddingBottom: 4}}>
					<InputLabel>Status</InputLabel>
					<Select
						value={filter}
						label="Status"
						onChange={handleStatusChange}
					>
						<MenuItem value="PENDING">Pending</MenuItem>
						<MenuItem value="PAID">Paid</MenuItem>
						<MenuItem value="CANCELLED">Cancelled</MenuItem>
						<MenuItem value="ALL">All</MenuItem>
					</Select>
				</FormControl>
			</Box>

			<DebtReminderTable status={filter === "ALL" ? "" : filter} type={currentType} />
		</Box>
	);
};

export default ViewListDebtReminder;
