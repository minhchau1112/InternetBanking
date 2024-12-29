import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface otpState {
    isOTPVerified: boolean;
    email: string | null;
}

const initialState: otpState = {
    isOTPVerified: false,
    email: null,
};

const otpSlice = createSlice({
    name: "otp",
    initialState,
    reducers: {
        verifyOTP: (state) => {
            state.isOTPVerified = true;
        },
        setEmail: (state, action: PayloadAction<string>) => {
            state.email = action.payload;
        },
        resetOTP: (state) => {
            state.isOTPVerified = false;
            state.email = null;
        },
    },
});

export const { verifyOTP, setEmail, resetOTP } = otpSlice.actions;
export default otpSlice.reducer;