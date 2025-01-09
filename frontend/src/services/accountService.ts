import axios from 'axios';

const API_URL = 'http://localhost:8888/api/accounts';

export const fetchAccount = async (
    accountNumber: string,
    accessToken: string
) => {
    try {
        const response = await axios.get(`${API_URL}/v2/${accountNumber}`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
            },
            withCredentials: true,
        });

        console.log("fetchAccount: ", response.data);

        return response.data;
    } catch (error) {
        console.error('Error fetching account:', error);
		throw error;
    }
};