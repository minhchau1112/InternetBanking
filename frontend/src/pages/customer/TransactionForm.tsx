import { useState } from 'react';
import axios from 'axios';
import {toast, ToastContainer} from "react-toastify";

const TransactionForm = () => {
    const [amount, setAmount] = useState('');
    const [fee, setFee] = useState('');
    const [destinationAccount, setDestinationAccount] = useState('');
    const [feePayer, setFeePayer] = useState('SENDER');
    const [type, setType] = useState('TRANSFER');
    const [message, setMessage] = useState('');
    const [otp, setOtp] = useState('');
    const [transactionId, setTransactionId] = useState(null);
    const [otpSent, setOtpSent] = useState(false);
    const [loading, setLoading] = useState(false);

    const sourceAccountId = localStorage.getItem('accountId');
    const accessToken = localStorage.getItem('access_token');

    const handleTransaction = async () => {
        setLoading(true); // Bắt đầu tải dữ liệu
        try {
            const response = await axios.post(
                'http://localhost:8888/api/transactions/create',
                {
                    sourceAccountId,
                    destinationAccountNumber: destinationAccount,
                    amount,
                    fee,
                    feePayer,
                    type,
                    message,
                },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            );
            setTransactionId(response.data.data.transactionId);
            setOtpSent(true);
        } catch (error) {
            console.error('Error creating transaction:', error);
            if (error.response && error.response.data) {
                toast.error(error.response.data); // Hiển thị lỗi từ server
            } else {
                toast.error('An unexpected error occurred.');
            }
        } finally {
            setLoading(false); // Kết thúc tải dữ liệu
        }
    };

    const handleOtpVerification = async () => {
        try {
            const response = await axios.post(
                'http://localhost:8888/api/transactions/verify-otp',
                {
                    transactionId,
                    otp,
                },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            );
            // Hiển thị thông báo thành công
            toast.success('Transaction completed successfully!');

            // Trì hoãn reload để người dùng kịp thấy thông báo
            setTimeout(() => {
                window.location.reload(); // Reload lại trang sau 2 giây
            }, 3000);
        } catch (error) {
            console.error('Error verifying OTP:', error);
            toast.error('Invalid or expired OTP!');
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
            <div className="bg-white shadow-lg rounded-lg p-8 w-full max-w-lg">
                <div className="relative">
                    <h2 className="text-2xl font-semibold text-gray-700 mb-6">Transaction Form</h2>
                    <div className="space-y-4">
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Destination Account Number</label>
                            <input
                                type="text"
                                placeholder="Enter destination account"
                                value={destinationAccount}
                                onChange={(e) => setDestinationAccount(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white placeholder-gray-400"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Amount</label>
                            <input
                                type="number"
                                placeholder="Enter amount"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white placeholder-gray-400"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Fee</label>
                            <input
                                type="number"
                                placeholder="Enter fee"
                                value={fee}
                                onChange={(e) => setFee(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white placeholder-gray-400"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Fee Payer</label>
                            <select
                                value={feePayer}
                                onChange={(e) => setFeePayer(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white"
                            >
                                <option value="SENDER">Sender</option>
                                <option value="RECEIVER">Receiver</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Transaction Type</label>
                            <select
                                value={type}
                                onChange={(e) => setType(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white"
                            >
                                <option value="TRANSFER">Transfer</option>
                                <option value="DEPOSIT">Deposit</option>
                                <option value="DEBT_REMINDER">Debt Reminder</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Message</label>
                            <textarea
                                placeholder="Enter message"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white placeholder-gray-400"
                            />
                        </div>
                    </div>
                    <button
                        onClick={handleTransaction}
                        disabled={loading} // Disable khi đang tải
                        className={`py-2 px-6 rounded-lg font-semibold ${
                            loading
                                ? 'bg-gray-500 text-gray-300 cursor-not-allowed'
                                : 'bg-blue-500 text-white hover:bg-blue-600'
                        }`}
                    >
                        {loading ? 'Submitting...' : 'Submit'}
                    </button>

                    {otpSent && (
                        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm">
                            <div className="bg-white p-6 rounded-lg shadow-lg w-[90%] max-w-md">
                                <h3 className="text-lg font-bold mb-4">OTP Verification</h3>
                                <input
                                    type="text"
                                    placeholder="Enter OTP"
                                    value={otp}
                                    onChange={(e) => setOtp(e.target.value)}
                                    className="w-full p-3 border border-gray-300 rounded-lg mb-4 text-white"
                                />
                                <div className="flex justify-end space-x-4">
                                    <button
                                        onClick={handleOtpVerification}
                                        className="bg-blue-500 text-white font-semibold py-2 px-6 rounded-lg hover:bg-blue-600"
                                    >
                                        Verify OTP
                                    </button>
                                    <button
                                        onClick={() => setOtpSent(false)}
                                        className="bg-gray-300 text-gray-700 font-semibold py-2 px-6 rounded-lg hover:bg-gray-400"
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
                <ToastContainer/>
            </div>
        </div>
    );
};

export default TransactionForm;
