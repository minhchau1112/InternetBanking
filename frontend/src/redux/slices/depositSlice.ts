import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface DepositState {
    isLoading: boolean;
    successMessage: string | null;
    errorMessage: string | null;
}

const initialState: DepositState = {
    isLoading: false,
    successMessage: null,
    errorMessage: null,
};

const depositSlice = createSlice({
    name: 'deposit',
    initialState,
    reducers: {
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.isLoading = action.payload;
        },
        setSuccessMessage: (state, action: PayloadAction<string>) => {
            state.successMessage = action.payload;
        },
        setErrorMessage: (state, action: PayloadAction<string>) => {
            state.errorMessage = action.payload;
        },
    },
});

export const { setLoading, setSuccessMessage, setErrorMessage } = depositSlice.actions;
export default depositSlice.reducer;
