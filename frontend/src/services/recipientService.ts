import axios from 'axios';

const API_URL = 'http://localhost:8888/api/v2/recipients';

export const fetchRecipients = async (customerId: number) => {
    try {
        const response = await axios.get(`${API_URL}/${customerId}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching recipients:', error);
		throw error;
    }
};