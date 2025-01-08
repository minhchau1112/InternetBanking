import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../slices/authSlice.ts";
import otpReducer from "../slices/otpSlice.ts";
import transactionsReducer from "../slices/transactionsSlice";

export const store = configureStore({
    reducer: {
        auth: authReducer,
        otp: otpReducer,
        transactions: transactionsReducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;