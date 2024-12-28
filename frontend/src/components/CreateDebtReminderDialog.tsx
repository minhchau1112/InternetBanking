import { Dialog, DialogActions, DialogContent, DialogTitle, TextField, Button, InputAdornment, Autocomplete } from "@mui/material";
import { useState, useEffect } from "react";

interface Debtor {
  name: string;
  accountNumber: string;
}

interface DebtReminderDialogProps {
  open: boolean;
  onClose: () => void;
  onCreate: (debtorInfo: { accountNumber: string; debtAmount: string; debtContent: string }) => void;
  savedDebtors: Debtor[];  // Giả sử đây là danh sách người nợ đã lưu trong frontend
  fetchDebtorInfo: (accountNumber: string) => Promise<Debtor | null>; // Hàm gọi API để lấy thông tin người nợ
}

const CreateDebtReminderDialog: React.FC<DebtReminderDialogProps> = ({
  open,
  onClose,
  onCreate,
  savedDebtors,
  fetchDebtorInfo,
}) => {
  const [debtorAccount, setDebtorAccount] = useState("");
  const [debtorName, setDebtorName] = useState("");
  const [debtAmount, setDebtAmount] = useState("");
  const [debtContent, setDebtContent] = useState("");
  const [error, setError] = useState(""); // Thông báo lỗi nếu không tìm thấy người nợ

  useEffect(() => {
    // Tự động tìm kiếm người nợ khi số tài khoản thay đổi
    if (debtorAccount) {
      const debtor = savedDebtors.find((d) => d.accountNumber === debtorAccount);
      if (debtor) {
        setDebtorName(debtor.name);
        setError("");
      } else {
        // Gọi API để tìm thông tin người nợ từ hệ thống
        fetchDebtorInfo(debtorAccount).then((data) => {
          if (data) {
            setDebtorName(data.name);
            setError("");
          } else {
            setDebtorName("");
            setError("Không tìm thấy người nợ với số tài khoản này.");
          }
        });
      }
    } else {
      setDebtorName(""); // Reset nếu số tài khoản trống
    }
  }, [debtorAccount, savedDebtors, fetchDebtorInfo]);

  const handleCreate = () => {
    if (debtorAccount && debtAmount && debtContent) {
      onCreate({ accountNumber: debtorAccount, debtAmount, debtContent });
      setDebtorAccount("");
      setDebtorName("");
      setDebtAmount("");
      setDebtContent("");
      onClose();
    } else {
      alert("Vui lòng điền đầy đủ thông tin.");
    }
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Tạo Nhắc Nợ</DialogTitle>
      <DialogContent>
        {/* Nhập số tài khoản người nợ với Autocomplete */}
        <Autocomplete
          freeSolo
          options={savedDebtors.map((debtor) => debtor.accountNumber)}
          onInputChange={(event, value) => setDebtorAccount(value)}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Số Tài Khoản Người Nợ"
              variant="outlined"
              fullWidth
              margin="normal"
            />
          )}
        />

        {/* Tên người nợ sẽ tự động điền khi tìm thấy thông tin */}
        <TextField
          label="Tên Người Nợ"
          variant="outlined"
          fullWidth
          margin="normal"
          value={debtorName}
          disabled
        />

        {/* Thông báo lỗi nếu không tìm thấy người nợ */}
        {error && <div style={{ color: "red" }}>{error}</div>}

        {/* Nhập số tiền */}
        <TextField
          label="Số Tiền"
          variant="outlined"
          fullWidth
          margin="normal"
          type="number"
          value={debtAmount}
          onChange={(e) => setDebtAmount(e.target.value)}
          InputProps={{
            startAdornment: <InputAdornment position="start">₫</InputAdornment>,
          }}
        />

        {/* Nhập nội dung nhắc nợ */}
        <TextField
          label="Nội Dung"
          variant="outlined"
          fullWidth
          margin="normal"
          value={debtContent}
          onChange={(e) => setDebtContent(e.target.value)}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">
          Hủy
        </Button>
        <Button onClick={handleCreate} color="primary">
          Gửi Nhắc Nợ
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CreateDebtReminderDialog;
