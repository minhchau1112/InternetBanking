import { createSlice, PayloadAction, createAsyncThunk } from "@reduxjs/toolkit";
import { fetchRecipients } from "../../services/recipientService";

interface Debtor {
  name: string;
  aliasName: string;
  accountNumber: string;
}

interface DebtReminderState {
  tab: number;
  filter: string;
  savedDebtors: Debtor[];
  isDialogOpen: boolean;
  loading: boolean;
  error: string | null;
}

const initialState: DebtReminderState = {
  tab: 0,
  filter: "PENDING",
  savedDebtors: [],
  isDialogOpen: false,
  loading: false,
  error: null,
};

export const fetchDebtors = createAsyncThunk<Debtor[], number, { state: any }>(
  "debtReminder/fetchDebtors",
  async (customerId, { getState, rejectWithValue }) => {
    const accessToken = localStorage.getItem("access_token") || "";
    try {
      const response = await fetchRecipients(customerId, accessToken);
      if (response.status === 200) {
        return response.data.map((item: any) => ({
          name: item.name,
		      aliasName: item.alias_name,
          accountNumber: item.account_number,
        }));
      } else {
        return rejectWithValue(response.message);
      }
    } catch (error) {
      return rejectWithValue("Failed to fetch debtors.");
    }
  }
);

const debtReminderSlice = createSlice({
  name: "debtReminder",
  initialState,
  reducers: {
    setTab(state, action: PayloadAction<number>) {
      state.tab = action.payload;
    },
    setFilter(state, action: PayloadAction<string>) {
      state.filter = action.payload;
    },
	setDialogOpen(state, action: PayloadAction<boolean>) {
		state.isDialogOpen = action.payload;
	},
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchDebtors.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchDebtors.fulfilled, (state, action) => {
        state.loading = false;
        state.savedDebtors = action.payload;
      })
      .addCase(fetchDebtors.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { setTab, setFilter, setDialogOpen } = debtReminderSlice.actions;

export default debtReminderSlice.reducer;