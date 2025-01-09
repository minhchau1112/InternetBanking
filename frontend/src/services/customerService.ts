import axios from 'axios';

const API_URL = 'http://localhost:8888/api/customer';

export const fetchCustomer = async (
    id: number,
    accessToken: string
) => {
    try {
        const response = await axios.get(`${API_URL}/${id}`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
            },
            withCredentials: true,
        });

        console.log("fetchCustomer: ", response.data);

        return response.data;
    } catch (error) {
        console.error('Error fetching customer:', error);
		throw error;
    }
};