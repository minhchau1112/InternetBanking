import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface SearchState {
    usernameSearchResult: string | null;
    accountSearchResult: string | null;
    rawAccountNumber: string;
}

const initialState: SearchState = {
    usernameSearchResult: null,
    accountSearchResult: null,
    rawAccountNumber: '',
};

const searchSlice = createSlice({
    name: 'search',
    initialState,
    reducers: {
        setUsernameSearchResult: (state, action: PayloadAction<string | null>) => {
            state.usernameSearchResult = action.payload;
        },
        setAccountSearchResult: (state, action: PayloadAction<string | null>) => {
            state.accountSearchResult = action.payload;
        },
        setRawAccountNumber: (state, action: PayloadAction<string>) => {
            state.rawAccountNumber = action.payload;
        },
    },
});

export const { setUsernameSearchResult, setAccountSearchResult, setRawAccountNumber } = searchSlice.actions;
export default searchSlice.reducer;
