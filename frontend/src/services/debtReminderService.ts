import axios from 'axios';

const API_URL = 'http://localhost:8888/api/debt-reminders';

export const fetchDebtRemindersForCreator = async (id: number, status: string, page: number, pageSize: number) => {
	try {
		const response = await axios.get(`${API_URL}/creator/${id}?status=${status}&page=${page}&size=${pageSize}`);
		return response.data.data;
	} catch (error) {
		console.error('Error fetching debt reminders:', error);
		throw error;
	}
};


export const fetchDebtRemindersForDebtor = async (id: number, status: string, page: number, pageSize: number) => {
	try {
		const response = await axios.get(`${API_URL}/debtor/${id}?status=${status}&page=${page}&size=${pageSize}`);
		return response.data.data;
	} catch (error) {
		console.error('Error fetching debt reminders:', error);
		throw error;
	}
};

export const cancelDebtReminder = async (debtReminderId: number, requesterAccountId: number, reason: string) => {
	try {
		// const response = await axios.put(`${API_URL}/cancel/${debtReminderId}?requesterAccountId=${requesterAccountId}`);
		// return response.data.data;
		try {
			const response = await axios.put(`${API_URL}/cancel/${debtReminderId}?requesterAccountId=${requesterAccountId}`, {
				reason: reason,
			}, {
				headers: {
					'Content-Type': 'application/json',
				},
				withCredentials: true, 
			});
	
			return response.data;
		} catch (error) {
			console.error('Error updating debt reminder status:', error);
			throw error;
		}

	} catch (error) {
		console.error('Error cancel debt reminder:', error);
		throw error;
	}
};
