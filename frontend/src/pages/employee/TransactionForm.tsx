import { useState } from 'react';
import axios from 'axios';

const TransactionForm = () => {
    const [amount, setAmount] = useState('');
    const [fee, setFee] = useState('');
    const [destinationAccount, setDestinationAccount] = useState('');
    const [feePayer, setFeePayer] = useState('SENDER');
    const [type, setType] = useState('TRANSFER');
    const [message, setMessage] = useState('');
    const [otp, setOtp] = useState('');  // Trạng thái OTP
    const [transactionId, setTransactionId] = useState(null); // Lưu ID giao dịch
    const [otpSent, setOtpSent] = useState(false); // Kiểm tra xem OTP đã gửi chưa

    const sourceAccountId = localStorage.getItem('accountId');
    const accessToken = localStorage.getItem('access_token');

    const handleTransaction = async () => {
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
                    message
                },
                {
                    headers: {
                        'Authorization': `Bearer ${accessToken}`
                    }
                }
            );
            console.log('Transaction successful:', response.data.data.message);
            setTransactionId(response.data.data.transactionId);
            setOtpSent(true);
        } catch (error) {
            console.error('Error creating transaction:', error);
        }
    };

    const handleOtpVerification = async () => {
        try {
            // Xác thực OTP
            const response = await axios.post(
                'http://localhost:8888/api/transactions/verify-otp',
                {
                    transactionId,
                    otp
                },
                {
                    headers: {
                        'Authorization': `Bearer ${accessToken}`
                    }
                }
            );
            console.log('Transaction completed:', response.data);
            alert('Transaction completed successfully!');
        } catch (error) {
            console.error('Error verifying OTP:', error);
            alert('Invalid or expired OTP!');
        }
    };

    return (
        <div>
            <h2>Transaction Form</h2>
            <input
                type="text"
                placeholder="Destination Account Number"
                value={destinationAccount}
                onChange={(e) => setDestinationAccount(e.target.value)}
            />
            <input
                type="number"
                placeholder="Amount"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
            />
            <input
                type="number"
                placeholder="Fee"
                value={fee}
                onChange={(e) => setFee(e.target.value)}
            />
            <select onChange={(e) => setFeePayer(e.target.value)} value={feePayer}>
                <option value="SENDER">Sender</option>
                <option value="RECEIVER">Receiver</option>
            </select>
            <select onChange={(e) => setType(e.target.value)} value={type}>
                <option value="TRANSFER">Transfer</option>
                <option value="DEPOSIT">Deposit</option>
                <option value="DEBT_REMINDER">Debt Reminder</option>
            </select>
            <textarea
                placeholder="Message"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
            />
            <button onClick={handleTransaction}>Submit</button>

            {otpSent && (
                <div>
                    <h3>OTP Verification</h3>
                    <input
                        type="text"
                        placeholder="Enter OTP"
                        value={otp}
                        onChange={(e) => setOtp(e.target.value)}
                    />
                    <button onClick={handleOtpVerification}>Verify OTP</button>
                </div>
            )}
        </div>
    );
};

export default TransactionForm;
