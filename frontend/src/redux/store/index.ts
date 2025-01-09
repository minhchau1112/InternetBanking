import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../slices/authSlice.ts";
import otpReducer from "../slices/otpSlice.ts";
import debtReminderReducer from "../slices/debtReminderSlice.ts";
import debtReminderTableReducer from "../slices/debtReminderTableSlice.ts";
import debtReminderCreateReducer from "../slices/debtReminderCreateSlice.ts";

export const store = configureStore({
    reducer: {
        auth: authReducer,
        otp: otpReducer,
        debtReminder: debtReminderReducer,
        debtReminderTable: debtReminderTableReducer,
        debtReminderCreateReducer: debtReminderCreateReducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;