import axios from 'axios';

const API_URL = 'http://localhost:8888/api/recipients';

export const fetchRecipients = async (
    customerId: number,
    accessToken: string
) => {
    try {
        const response = await axios.get(`${API_URL}/v2/${customerId}`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
            },
            withCredentials: true,
        });

        console.log("fetchRecipients: ", response.data);

        return response.data;
    } catch (error) {
        console.error('Error fetching recipients:', error);
		throw error;
    }
};