import { useState, useEffect } from "react";
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
  Button,
} from "@mui/material";
import CreateDebtReminderDialog from "../../components/CreateDebtReminderDialog";
import { fetchRecipients } from "../../services/recipientService";

interface Debtor {
  name: string;
  accountNumber: string;
}

const ViewListDebtReminder = () => {
  const [tab, setTab] = useState(0);
  const [filter, setFilter] = useState("PENDING");
  const [openDialog, setOpenDialog] = useState(false);
  const [savedDebtors, setSavedDebtors] = useState<Debtor[]>([]);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTab(newValue);
  };

  const handleStatusChange = (e: SelectChangeEvent<string>) => {
    const selectedValue = e.target.value;
    setFilter(selectedValue);
  };

  const handleCreateDebtReminder = (debtorInfo: {
    accountNumber: string;
    debtAmount: string;
    debtContent: string;
  }) => {
    console.log("Tạo nhắc nợ:", debtorInfo);
    // Logic gửi nhắc nợ đến người nợ (API call hoặc xử lý logic ở đây)
    setOpenDialog(false);
  };

  const handleDialogClose = () => {
    setOpenDialog(false);
  };

  const fetchDebtors = async (customerId: number): Promise<Debtor[]> => {
    try {
      const response = await fetchRecipients(customerId);
	  console.log("response: ", response);

      if (response.success) {
        console.log("response: ", response);
        return response.data.map((item: any, index: number) => ({
			name: item.name,
			accountNumber: item.account_number
		}))
      } else {
        console.error("Không thành công: ", response.message);
        return [];
      }
    } catch (error) {
      console.error("Lỗi khi fetch debtors: ", error);
      return [];
    }
  };

  const fetchDebtorInfo = async (accountNumber: string): Promise<Debtor | null> => {
    try {
      const tmp = { name: "John Doe", accountNumber: "12345" }; // Dữ liệu giả lập
      return tmp;
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  useEffect(() => {
    // Gọi fetchDebtors khi component được mount
    const loadDebtors = async () => {
      const customerId = 3; // Thay bằng ID khách hàng hiện tại
      const debtors = await fetchDebtors(customerId);
      setSavedDebtors(debtors);
    };

    loadDebtors();
  }, []);

  const currentType = tab === 0 ? "Creator" : "Debtor";

  return (
    <Box sx={{ paddingLeft: 4, paddingTop: 4 }}>
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

        {/* Nút tạo nhắc nợ */}
        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpenDialog(true)}
        >
          Tạo nhắc nợ
        </Button>
      </Box>

      <DebtReminderTable status={filter === "ALL" ? "" : filter} type={currentType} />

      {/* Dialog tạo nhắc nợ */}
      <CreateDebtReminderDialog
        open={openDialog}
        onClose={handleDialogClose}
        onCreate={handleCreateDebtReminder}
        savedDebtors={savedDebtors} // Truyền danh sách người nợ
        fetchDebtorInfo={fetchDebtorInfo}
      />
    </Box>
  );
};

export default ViewListDebtReminder;