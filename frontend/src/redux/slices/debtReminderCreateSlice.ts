import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface DebtReminderState {
  debtorAccount: string;
  debtorName: string;
  debtAmount: string;
  debtContent: string;
  debtorId: number;
  error: string;
}

const initialState: DebtReminderState = {
  debtorAccount: '',
  debtorName: '',
  debtAmount: '',
  debtContent: '',
  debtorId: 0,
  error: '',
};

const debtReminderCreateSlice = createSlice({
  name: 'debtReminder',
  initialState,
  reducers: {
    setDebtorAccount(state, action: PayloadAction<string>) {
      state.debtorAccount = action.payload;
    },
    setDebtorName(state, action: PayloadAction<string>) {
      state.debtorName = action.payload;
    },
    setDebtAmount(state, action: PayloadAction<string>) {
      state.debtAmount = action.payload;
    },
    setDebtContent(state, action: PayloadAction<string>) {
      state.debtContent = action.payload;
    },
    setDebtorId(state, action: PayloadAction<number>) {
      state.debtorId = action.payload;
    },
    setError(state, action: PayloadAction<string>) {
      state.error = action.payload;
    },
    resetState(state) {
      state.debtorAccount = '';
      state.debtorName = '';
      state.debtAmount = '';
      state.debtContent = '';
      state.debtorId = 0;
      state.error = '';
    },
  },
});

export const { setDebtorAccount, setDebtorName, setDebtAmount, setDebtContent, setDebtorId, setError, resetState } = debtReminderCreateSlice.actions;
export default debtReminderCreateSlice.reducer;