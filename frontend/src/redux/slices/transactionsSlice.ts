import { createSlice, PayloadAction } from "@reduxjs/toolkit";

type BaseTransaction = {
    id: number;
    amount: number;
    date: string;
    description: string;
    accountNumber: string;
    tab: 'in' | 'out';
};

type InternalTransaction = BaseTransaction & {
    type: 'transfer' | 'debt_payment';
};

type InterbankTransaction = BaseTransaction & {
    bankName: string;
};

type CombinedTransaction = InternalTransaction | InterbankTransaction;

type TransactionsState = {
    allTransactions: CombinedTransaction[];
    filteredTransactions: CombinedTransaction[];
    activeTab: 'all' | 'in' | 'out';
    error: string | null;
    sortOrder: 'asc' | 'desc';
};

const initialState: TransactionsState = {
    allTransactions: [],
    filteredTransactions: [],
    activeTab: 'all',
    error: null,
    sortOrder: 'asc',
};

const transactionsSlice = createSlice({
    name: "transactions",
    initialState,
    reducers: {
        setTransactions(state, action: PayloadAction<CombinedTransaction[]>) {
            state.allTransactions = action.payload;
            state.filteredTransactions = action.payload;
        },
        setFilteredTransactions(state, action: PayloadAction<CombinedTransaction[]>) {
            state.filteredTransactions = action.payload;
        },
        setActiveTab(state, action: PayloadAction<'all' | 'in' | 'out'>) {
            state.activeTab = action.payload;
        },
        setError(state, action: PayloadAction<string | null>) {
            state.error = action.payload;
        },
        setSortOrder(state, action: PayloadAction<'asc' | 'desc'>) {
            state.sortOrder = action.payload;
        },
    },
});

export const {
    setTransactions,
    setFilteredTransactions,
    setActiveTab,
    setError,
    setSortOrder,
} = transactionsSlice.actions;

export default transactionsSlice.reducer;
