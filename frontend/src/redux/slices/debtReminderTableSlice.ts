import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface DebtReminderState {
  rows: any[];
  page: number;
  pageSize: number;
  rowCount: number;
  loading: boolean;
  selectedDebtId: number | 0;
  openDialog: boolean;
  openOtpDialog: boolean;
  otpError: string;
}

const initialState: DebtReminderState = {
  rows: [],
  page: 0,
  pageSize: 10,
  rowCount: 0,
  loading: false,
  selectedDebtId: 0,
  openDialog: false,
  openOtpDialog: false,
  otpError: "",
};

const debtReminderSlice = createSlice({
  name: "debtReminder",
  initialState,
  reducers: {
    setRows(state, action: PayloadAction<any[]>) {
      state.rows = action.payload;
    },
    setPage(state, action: PayloadAction<number>) {
      state.page = action.payload;
    },
    setPageSize(state, action: PayloadAction<number>) {
      state.pageSize = action.payload;
    },
    setRowCount(state, action: PayloadAction<number>) {
      state.rowCount = action.payload;
    },
    setLoading(state, action: PayloadAction<boolean>) {
      state.loading = action.payload;
    },
    setSelectedDebtId(state, action: PayloadAction<number | 0>) {
      state.selectedDebtId = action.payload;
    },
    setOpenDialog(state, action: PayloadAction<boolean>) {
      state.openDialog = action.payload;
    },
    setOpenOtpDialog(state, action: PayloadAction<boolean>) {
      state.openOtpDialog = action.payload;
    },
    setOtpError(state, action: PayloadAction<string>) {
      state.otpError = action.payload;
    },
  },
});

export const {
  setRows,
  setPage,
  setPageSize,
  setRowCount,
  setLoading,
  setSelectedDebtId,
  setOpenDialog,
  setOpenOtpDialog,
  setOtpError,
} = debtReminderSlice.actions;

export default debtReminderSlice.reducer;