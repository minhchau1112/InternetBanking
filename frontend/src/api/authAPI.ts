import axios from "axios";

const API_BASE_URL = "http://localhost:8888/api/auth";
const API_CUSTOMER_URL = "http://localhost:8888";

export const login = async (username: string, password: string, recaptchaResponse: string) => {
    const response = await axios.post(
        `${API_BASE_URL}/login`,
        { username, password },
        {
            params: { recaptchaResponse },
            withCredentials: true
        }
    );
    return response.data.data;
};
export const updatePassword = async (passwordData: { username: string; password: string }) => {
    try {
        const accessToken = localStorage.getItem('access_token');

        if (!accessToken) {
            throw new Error('Access token is missing. Please log in again.');
        }

        const response = await axios.put(`${API_CUSTOMER_URL}/customer`, passwordData, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        });

        return response.data;
    } catch (error) {
        console.error('Error during password update:', error);
        throw new Error('Error changing password');
    }
};
export const fetchCustomerDetails = async () => {
    try {
        const user = localStorage.getItem('user');
        const accessToken = localStorage.getItem('access_token');
        if (!user) {
            throw new Error('User information not found in localStorage');
        }

        const { username } = JSON.parse(user);
        if (!username) {
            throw new Error('Username is missing in localStorage');
        }

        const response = await axios.get(`${API_CUSTOMER_URL}/customer`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
            params: { username },
        });

        return response.data.data;
    } catch (error) {
        console.error(error);
    }
};
export const logoutUser = async (): Promise<void> => {
    try {
        const accessToken = localStorage.getItem("access_token");

        if (!accessToken) {
            throw new Error("No access token found.");
        }

        await axios.post(
            `${API_BASE_URL}/logout`,
            {},
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true,
            }
        );

        console.log("User logged out successfully.");
    } catch (error) {
        console.error("Error logging out:", error);
        throw error instanceof Error ? error : new Error("An unknown error occurred.");
    }
};
export const fetchCustomerAccounts = async () => {
    try {
        const user = localStorage.getItem('user');
        const accessToken = localStorage.getItem('access_token');
        if (!user) {
            throw new Error('User information not found in localStorage');
        }

        const { userID } = JSON.parse(user);
        if (!userID) {
            throw new Error('userID is missing in localStorage');
        }

        const response = await axios.get(`${API_CUSTOMER_URL}/api/accounts/list/${userID}`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        });
        console.log(response.data.data);
        return response.data.data;
    } catch (error) {
        console.error(error);
    }
};
export const closeAccountAPI = async (accountNumber: string) => {
    try {
        const accessToken = localStorage.getItem('access_token');
        const response = await axios.delete(`${API_CUSTOMER_URL}/api/accounts/${accountNumber}`, {
            headers: { Authorization: `Bearer ${accessToken}` }
        });
        return response.data;
    } catch (error) {
        console.error(error);
    }
};



