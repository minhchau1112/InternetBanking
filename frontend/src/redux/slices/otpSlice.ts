import { createSlice } from "@reduxjs/toolkit";

interface OtpState {
    isOTPVerified: boolean;
}

const initialState: OtpState = {
    isOTPVerified: false,
};

const otpSlice = createSlice({
    name: "otp",
    initialState,
    reducers: {
        verifyOTP: (state) => {
            state.isOTPVerified = true;
        },
        resetOTP: (state) => {
            state.isOTPVerified = false;
        },
    },
});

export const { verifyOTP, resetOTP } = otpSlice.actions;
export default otpSlice.reducer;
