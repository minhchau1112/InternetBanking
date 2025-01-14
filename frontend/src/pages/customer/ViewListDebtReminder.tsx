import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { RootState, AppDispatch } from "../../redux/store";
import { setTab, setFilter, setDialogOpen, fetchDebtors } from "../../redux/slices/debtReminderSlice";
import DebtReminderTable from "../../components/DebtReminderTable";
import {
  FormControl,
  Select,
  MenuItem,
  InputLabel,
  Box,
  Tabs,
  Tab,
  Button,
  SelectChangeEvent
} from "@mui/material";
import CreateDebtReminderDialog from "../../components/CreateDebtReminderDialog";

const ViewListDebtReminder = () => {
  const dispatch: AppDispatch = useDispatch();

  const { tab, filter, savedDebtors, isDialogOpen } = useSelector((state: RootState) => state.debtReminder);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    dispatch(setTab(newValue));
  };

  const handleStatusChange = (event: SelectChangeEvent<string>) => {
    const selectedValue = event.target.value; // `value` sẽ là kiểu `string` do khai báo generic
    dispatch(setFilter(selectedValue));
  };

  const handleCreateDebtReminder = (debtorInfo: {
    accountNumber: string;
    debtAmount: string;
    debtContent: string;
  }) => {
    console.log("Tạo nhắc nợ:", debtorInfo);
    dispatch(setDialogOpen(false)); 
  };

  useEffect(() => {
    const user = localStorage.getItem("user") || "";
    const {userID} = JSON.parse(user);
    dispatch(fetchDebtors(userID));
  }, [dispatch]);

  const currentType = tab === 0 ? "Creator" : "Debtor";

  return (
    <Box sx={{ paddingLeft: 10, paddingRight: 10, paddingTop: 4 }}>
      <Tabs value={tab} onChange={handleTabChange}>
        <Tab label="Created by You" />
        <Tab label="Received from Others" />
      </Tabs>

      <Box className="flex justify-end" sx={{ paddingBottom: 4, gap: 1 }}>
        <FormControl size="small">
          <InputLabel>Status</InputLabel>
          <Select value={filter} label="Status" onChange={handleStatusChange}>
            <MenuItem value="PENDING">Pending</MenuItem>
            <MenuItem value="PAID">Paid</MenuItem>
            <MenuItem value="CANCELLED">Cancelled</MenuItem>
            <MenuItem value="ALL">All</MenuItem>
          </Select>
        </FormControl>

        <Button variant="contained" color="primary" onClick={() => dispatch(setDialogOpen(true))}>
          Tạo nhắc nợ
        </Button>
      </Box>

      <DebtReminderTable status={filter === "ALL" ? "" : filter} type={currentType} />

      <CreateDebtReminderDialog
        open={isDialogOpen}
        onClose={() => dispatch(setDialogOpen(false))}
        onCreate={handleCreateDebtReminder}
        savedDebtors={savedDebtors}
      />
    </Box>
  );
};

export default ViewListDebtReminder;
