import axios from 'axios';

const API_URL = 'http://localhost:8888/api/transfer/internal';

interface InternalTransferRequest {
	sourceAccountId: number,
	destinationAccountId: number,
	amount: number,
	message: string,
	feePayer: string,
}

export const initiateTransfer = async (
	{sourceAccountId, destinationAccountId, amount, message, feePayer}: InternalTransferRequest,
	accessToken: string
) => {
    try {
		const request = {
			source_account_id: sourceAccountId,
            destination_account_id: destinationAccountId,
			amount: amount,
			message: message,
			fee_payer: feePayer
		}
		console.log("Request: ", request);
        const response = await axios.post(`${API_URL}`, request, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
            },
            withCredentials: true,
        });

		console.log("response: ", response);
        return response.data;
    } catch (error) {
        console.error('Error during generate otp:', error);
        throw new Error('Payment failed');
    }
};