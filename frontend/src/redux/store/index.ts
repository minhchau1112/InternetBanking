import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../slices/authSlice.ts";
import otpReducer from "../slices/otpSlice.ts";

export const store = configureStore({
    reducer: {
        auth: authReducer,
        otp: otpReducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;