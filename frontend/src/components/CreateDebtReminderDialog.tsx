import { Dialog, DialogActions, DialogContent, DialogTitle, TextField, Button, InputAdornment, Box, List, ListItem, ListItemText } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import { RootState, AppDispatch } from "../redux/store";
import { setDebtorAccount, setDebtorName, setDebtAmount, setDebtContent, setDebtorId, setError, resetState } from "../redux/slices/debtReminderCreateSlice";
import { fetchAccount } from "../services/accountService";
import { setLoading } from "@/redux/slices/debtReminderTableSlice";
import { SearchOutlined } from '@mui/icons-material';
import { createDebtReminder } from "@/services/debtReminderService";
import { useSnackbar } from "notistack";

interface Debtor {
  name: string;
  aliasName: string;
  accountNumber: string;
}

interface DebtReminderDialogProps {
  open: boolean;
  onClose: () => void;
  onCreate: (debtorInfo: { debtorId: number, accountNumber: string; debtAmount: string; debtContent: string }) => void;
  savedDebtors: Debtor[];  
}

const CreateDebtReminderDialog: React.FC<DebtReminderDialogProps> = ({
  open,
  onClose,
  onCreate,
  savedDebtors,
}) => {
  const dispatch = useDispatch<AppDispatch>();
  const { debtorAccount, debtorName, debtAmount, debtContent, debtorId, error } = useSelector((state: RootState) => state.debtReminderCreateReducer);

  const { enqueueSnackbar } = useSnackbar();

  const retrieveInfo = async (account_number: string) => {
    const accessToken = localStorage.getItem('access_token') || "";
    dispatch(setLoading(true));
    try {
      const response = await fetchAccount(account_number, accessToken);
      if (response.status === 200) {
        const data = response.data;
        dispatch(setDebtorName(data.customer.name));
        dispatch(setDebtorId(data.id));
        dispatch(setError(''));
      } else {
        dispatch(setDebtorName(''));
        dispatch(setDebtorId(0));
        dispatch(setError('Không tìm thấy khách hàng với số tài khoản này.'));
      }
    } catch (error) {
      dispatch(setError('Lỗi khi lấy thông tin người nợ.'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const handleCreate = async () => {
    if (debtorAccount && debtAmount && debtContent) {
      if (!debtorId) {
        await retrieveInfo(debtorAccount);
        if (!debtorId) return;
      }

      const accountId = localStorage.getItem('accountId') || "";
      const accessToken = localStorage.getItem('access_token') || "";
      dispatch(setLoading(true));
      try {
        const response = await createDebtReminder(parseInt(accountId, 10), { debtorId, accountNumber: debtorAccount, debtAmount, debtContent }, accessToken);
        if (response.status === 201) {
          onCreate({ debtorId, accountNumber: debtorAccount, debtAmount, debtContent });
          dispatch(resetState());
          enqueueSnackbar('Tạo nhắc nợ thành công', { variant: 'success', autoHideDuration: 3000 });
          onClose();
        } else {
          dispatch(setError('Tạo nhắc nợ thất bại'));
          enqueueSnackbar('Tạo nhắc nợ thất bại', { variant: 'error', autoHideDuration: 3000 });
        }
      } catch (error) {
        dispatch(setError('Lỗi khi tạo nhắc nợ.'));
        enqueueSnackbar('Lỗi khi tạo nhắc nợ', { variant: 'error', autoHideDuration: 3000 });
      } finally {
        dispatch(setLoading(false));
      }
    } else {
      alert('Vui lòng điền đầy đủ thông tin.');
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="lg">
      <DialogTitle>Tạo Nhắc Nợ</DialogTitle>
      <DialogContent>
        <Box display="flex" justifyContent="space-between">
          <Box flex={1} paddingRight={2}>
            <Box display="flex" alignItems="center">
              <TextField
                label="Số tài khoản"
                variant="outlined"
                fullWidth
                margin="normal"
                value={debtorAccount}
                onChange={(e) => dispatch(setDebtorAccount(e.target.value))} 
              />
              <Button
                variant="contained"
                color="primary"
                onClick={() => retrieveInfo(debtorAccount)} 
                sx={{
                  marginTop: 1,
                  marginLeft: 1,
                  height: "100%",
                  minHeight: "56px", 
                }}
              >
                <SearchOutlined />
              </Button>
            </Box>

            <Box>
              {error && <div style={{ color: "red" }}>{error}</div>}

              <TextField
                label="Tên khách hàng"
                variant="outlined"
                fullWidth
                margin="normal"
                value={debtorName}
                disabled
              />
            </Box>

            <TextField
              label="Số Tiền"
              variant="outlined"
              fullWidth
              margin="normal"
              type="number"
              value={debtAmount}
              onChange={(e) => dispatch(setDebtAmount(e.target.value))}
              InputProps={{
                startAdornment: <InputAdornment position="start">₫</InputAdornment>,
              }}
            />

            <TextField
              label="Nội Dung"
              variant="outlined"
              fullWidth
              margin="normal"
              value={debtContent}
              onChange={(e) => dispatch(setDebtContent(e.target.value))}
            />
          </Box>

          <Box flex={1} paddingLeft={2} borderLeft="1px solid #ccc" maxHeight="400px" overflow="auto">
          <Box fontSize={20} fontWeight="bold" textAlign="center" marginBottom={2}>
            Danh sách đã lưu
          </Box>
            
            <List>
              {savedDebtors.map((debtor) => (
                <ListItem
                  key={debtor.accountNumber}
                  component="button"
                  onClick={() => {
                    dispatch(setDebtorAccount(debtor.accountNumber));
                    dispatch(setDebtorName(debtor.name));
                    dispatch(setError('')); 
                  }}
                  sx={{
                    marginBottom: 1.5
                  }}
                >
                  <ListItemText
                    primary={`${debtor.aliasName} - ${debtor.accountNumber}`}
                  />
                </ListItem>
              ))}
            </List>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="outlined" color="secondary">
          Hủy
        </Button>
        <Button onClick={handleCreate} variant="contained" color="primary">
          Gửi Nhắc Nợ
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CreateDebtReminderDialog;
