export interface User {
    username: string;
    accountID: number | null;
    role: string;
}

export interface UserState {
    isAuthenticated: boolean;
    isRefreshToken: boolean;
    errorRefreshToken: string;
    user: User;
}
