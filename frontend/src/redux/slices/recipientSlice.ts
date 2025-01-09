import { createSlice, PayloadAction } from "@reduxjs/toolkit";

type Customer = {
    createdAt: string,
    email: string,
    id: number,
    name: string,
    password: string,
    phone: string,
    username: string,
}

type Recipient = {
    id: number,
    customer: Customer,
    accountNumber: string,
    aliasName: string,
    bankCode: string,
}

type RecipientsState = {
    allRecipients: Recipient[];
    filteredRecipients: Recipient[];
};

const initialState: RecipientsState = {
    allRecipients: [],
    filteredRecipients: []
};

const recipientsSlice = createSlice({
    name: "recipients",
    initialState,
    reducers: {
        setRecipients(state, action: PayloadAction<Recipient[]>) {
            state.allRecipients = action.payload;
            state.filteredRecipients = action.payload;
        },
        setFilteredRecipients(state, action: PayloadAction<Recipient[]>) {
            state.filteredRecipients = action.payload;
        }
    },
});

export const {
    setRecipients,
    setFilteredRecipients,
} = recipientsSlice.actions;

export default recipientsSlice.reducer;