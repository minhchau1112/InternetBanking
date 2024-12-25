import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface User {
    username: string;
    accountID: number;
    role: string;
}

interface UserState {
    user: User | null;
    isAuthenticated: boolean;
    isRefreshToken: boolean;
    errorRefreshToken: string;
}

const initialState: UserState = {
    user: null,
    isAuthenticated: false,
    isRefreshToken: false,
    errorRefreshToken: '',
};

const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        loginSuccess(state, action: PayloadAction<User>) {
            state.user = action.payload;
            state.isAuthenticated = true;
            state.errorRefreshToken = ''; // Clear errors on successful login
        },
        loginFailure(state) {
            state.isAuthenticated = false;
            state.errorRefreshToken = 'Đăng nhập thất bại, vui lòng kiểm tra lại thông tin.';
        },
        logout(state) {
            state.user = null;
            state.isAuthenticated = false;
        },
        setErrorRefreshToken(state, action: PayloadAction<string>) {
            state.errorRefreshToken = action.payload;
        },
    },
});

export const { loginSuccess, loginFailure, logout, setErrorRefreshToken } = userSlice.actions;

export default userSlice.reducer;
