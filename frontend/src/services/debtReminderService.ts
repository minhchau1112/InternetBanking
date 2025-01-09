import axios from 'axios';

const API_URL = 'http://localhost:8888/api/debt-reminders';

interface DebtorInfo { 
	debtorId: number, 
	accountNumber: string; 
	debtAmount: string; 
	debtContent: string 
}

export const createDebtReminder = async (
	creatorId: number,
	debtorInfor: DebtorInfo,
	accessToken: string,
) => {
	try {
		const response = await axios.post(
			`${API_URL}`,
			{
				creator_account_id: creatorId,
				debtor_account_id: debtorInfor.debtorId,
				amount: debtorInfor.debtAmount,
				message: debtorInfor.debtContent,
			},
			{
				headers: {
					Authorization: `Bearer ${accessToken}`,
				},
			}
		);

		console.log("createDebtReminder: ", response);
		return response.data;
	} catch (error) {
		console.error('Error fetching debt reminders:', error);
		throw error;
	}
}
// Fetch debt reminders for creator
export const fetchDebtRemindersForCreator = async (
	id: number,
	status: string,
	page: number,
	pageSize: number,
	accessToken: string
) => {
	try {
		const response = await axios.get(
			`${API_URL}/creator/${id}?status=${status}&page=${page}&size=${pageSize}`,
			{
				headers: {
					Authorization: `Bearer ${accessToken}`,
				},
			}
		);

		return response.data.data;
	} catch (error) {
		console.error('Error fetching debt reminders:', error);
		throw error;
	}
};

// Fetch debt reminders for debtor
export const fetchDebtRemindersForDebtor = async (
	id: number,
	status: string,
	page: number,
	pageSize: number,
	accessToken: string
) => {
	try {
		const response = await axios.get(
			`${API_URL}/debtor/${id}?status=${status}&page=${page}&size=${pageSize}`,
			{
				headers: {
					Authorization: `Bearer ${accessToken}`,
				},
			}
		);

		console.log("Fetch 2:", response.data.data);
		return response.data.data;
	} catch (error) {
		console.error('Error fetching debt reminders:', error);
		throw error;
	}
};

// Cancel debt reminder
export const cancelDebtReminder = async (
	debtReminderId: number,
	requesterAccountId: number,
	reason: string,
	accessToken: string
) => {
	try {
		const response = await axios.put(
			`${API_URL}/cancel/${debtReminderId}?requesterAccountId=${requesterAccountId}`,
			{
				reason: reason,
			},
			{
				headers: {
					Authorization: `Bearer ${accessToken}`,
					'Content-Type': 'application/json',
				},
				withCredentials: true,
			}
		);

		return response.data;
	} catch (error) {
		console.error('Error cancel debt reminder: ', error);
		throw error;
	}
};

// Pay debt reminder
export const payDebtReminder = async (
	debtReminderId: number,
	otp: string,
	email: string,
	accessToken: string
) => {
	try {
		const response = await axios.post(
			`${API_URL}/${debtReminderId}/pay`,
			{
				email: email,
				otp: otp,
			},
			{
				headers: {
					Authorization: `Bearer ${accessToken}`,
				},
			}
		);
		return response.data;
	} catch (error) {
		console.error('Error during payment:', error);
		throw new Error('Payment failed');
	}
};