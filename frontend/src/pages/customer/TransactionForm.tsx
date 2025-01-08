import { useState, useEffect } from 'react';
import axios from 'axios';
import { toast, ToastContainer } from 'react-toastify';

const TransactionForm = () => {
    const [activeTab, setActiveTab] = useState('internal'); // "internal" or "external"
    const [accounts, setAccounts] = useState([]);
    const [recipients, setRecipients] = useState([]);
    const [selectedAccount, setSelectedAccount] = useState('');
    const [recipientOption, setRecipientOption] = useState('choose'); // "choose" or "manual"
    const [destinationAccount, setDestinationAccount] = useState('');
    const [selectedRecipient, setSelectedRecipient] = useState('');
    const [amount, setAmount] = useState('');
    const [message, setMessage] = useState('');
    const [otp, setOtp] = useState('');
    const [otpSent, setOtpSent] = useState(false);
    const [transactionId, setTransactionId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [fee, setFee] = useState('');
    const [feePayer, setFeePayer] = useState('SENDER');

    const sourceAccountId = localStorage.getItem('accountId');
    const accessToken = localStorage.getItem('access_token');

    useEffect(() => {
        // Fetch user's accounts and recipients when the component mounts
        const fetchAccountsAndRecipients = async () => {
            try {
                const accountResponse = await axios.get(`http://localhost:8888/api/accounts/accountInfo/${sourceAccountId}`, {
                    headers: { Authorization: `Bearer ${accessToken}` },
                });
                console.log("account from user id"+JSON.stringify(accountResponse.data, null, 2));
                setAccounts(accountResponse.data.data);

                const recipientResponse = await axios.get(`http://localhost:8888/api/recipients/${sourceAccountId}`, {
                    headers: { Authorization: `Bearer ${accessToken}` },
                });
                console.log("account from user id"+JSON.stringify(recipientResponse.data, null, 2));
                setRecipients(recipientResponse.data.data);
            } catch (error) {
                toast.error('Error fetching accounts or recipients.');
            }
        };
        fetchAccountsAndRecipients();
    }, [accessToken]);

    const handleTransaction = async () => {
        setLoading(true);
        try {
            const response = await axios.post(
                'http://localhost:8888/api/transactions/create',
                {
                    sourceAccountId: selectedAccount,
                    destinationAccountNumber: destinationAccount,
                    amount,
                    message,
                },
                {
                    headers: { Authorization: `Bearer ${accessToken}` },
                }
            );
            setTransactionId(response.data.transactionId);
            setOtpSent(true);
        } catch (error) {
            toast.error('Error creating transaction.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (amount) {
            const calculatedFee = (parseFloat(amount) * 0.05).toFixed(2); // Fee is 5% of the amount
            setFee(calculatedFee); // Update the fee state
        }
    }, [amount]);

    const handleOtpVerification = async () => {
        try {
            await axios.post(
                'http://localhost:8888/api/transactions/verify-otp',
                { transactionId, otp },
                { headers: { Authorization: `Bearer ${accessToken}` } }
            );
            toast.success('Transaction completed successfully!');
            setTimeout(() => window.location.reload(), 3000);
        } catch (error) {
            toast.error('Invalid or expired OTP.');
        }
    };

    return (
        <div className="w-full h-full flex flex-col bg-gray-100 mt-5">
            {/* Tabs */}
            <div className="flex justify-center border-b border-gray-300">
                <button
                    onClick={() => setActiveTab('internal')}
                    className={`w-1/2 py-4 text-center font-semibold rounded-none ${
                        activeTab === 'internal' ? 'border-b-4 border-blue-500 text-blue-500' : 'text-gray-500'
                    }`}
                >
                    Internal Transfer
                </button>
                <button
                    onClick={() => setActiveTab('external')}
                    className={`w-1/2 py-4 text-center font-semibold rounded-none ${
                        activeTab === 'external' ? 'border-b-4 border-blue-500 text-blue-500' : 'text-gray-500'
                    }`}
                >
                    External Transfer
                </button>
            </div>

            {/* Form Content */}
            <div className="flex-grow p-6 w-full h-full">
                <div className="max-w-lg mx-auto bg-white shadow-md rounded-lg p-6">
                    {/* Select account to pay from */}
                    <div className="mb-4">
                        <label className="block text-gray-700 font-medium mb-2">Select Account</label>
                        <select
                            value={selectedAccount}
                            onChange={(e) => setSelectedAccount(e.target.value)}
                            className="w-full p-3 border border-gray-300 rounded-lg"
                        >
                            <option value="">Select an account</option>
                            <option key={sourceAccountId} value={sourceAccountId}>
                                {accounts.accountNumber} - Balance: {accounts.balance} VND
                            </option>
                        </select>
                    </div>

                    {/* Recipient Selection */}
                    <div className="mb-4">
                        <label className="block text-gray-700 font-medium mb-2">Transfer Method</label>
                        <div className="flex items-center space-x-4">
                            <label className="flex items-center space-x-2">
                                <input
                                    type="radio"
                                    value="choose"
                                    checked={recipientOption === 'choose'}
                                    onChange={() => setRecipientOption('choose')}
                                    className="form-radio"
                                />
                                <span>Choose Recipient</span>
                            </label>
                            <label className="flex items-center space-x-2">
                                <input
                                    type="radio"
                                    value="manual"
                                    checked={recipientOption === 'manual'}
                                    onChange={() => setRecipientOption('manual')}
                                    className="form-radio"
                                />
                                <span>Enter Manually</span>
                            </label>
                        </div>
                    </div>

                    {/* Conditional Rendering for Recipient */}
                    {recipientOption === 'choose' ? (
                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">Recipient</label>
                            <select
                                value={selectedRecipient}
                                onChange={(e) => setSelectedRecipient(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg"
                            >
                                <option value="">Select a recipient</option>
                                {recipients.map((recipient) => (
                                    <option key={recipient.id} value={recipient.accountNumber}>
                                        {recipient.aliasName} - {recipient.accountNumber} - {recipient.bankCode}
                                    </option>
                                ))}
                            </select>
                        </div>
                    ) : (
                        <div className="mb-4">
                            <label className="block text-gray-700 font-medium mb-2">Destination Account Number</label>
                            <input
                                type="text"
                                value={destinationAccount}
                                onChange={(e) => setDestinationAccount(e.target.value)}
                                placeholder="Enter destination account"
                                className="w-full p-3 border border-gray-300 rounded-lg"
                            />
                        </div>
                    )}

                    {/* Amount and Message */}
                    <div className="mb-4">
                        <label className="block text-gray-700 font-medium mb-2">Amount</label>
                        <input
                            type="number"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            placeholder="Enter amount"
                            className="w-full p-3 border border-gray-300 rounded-lg"
                        />
                    </div>
                    <div className="flex space-x-4">
                        <div className="w-full">
                            <label className="block text-gray-700 font-medium mb-2">Fee</label>
                            <input
                                type="number"
                                placeholder="Enter fee"
                                value={fee}
                                onChange={(e) => setFee(e.target.value)} // You can still allow the user to modify the fee if needed
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-200 text-black h-12 placeholder-gray-400"
                                disabled // Disable input since fee is calculated automatically
                            />
                        </div>
                        <div className="w-full">
                            <label className="block text-gray-700 font-medium mb-2">Fee Payer</label>
                            <select
                                value={feePayer}
                                onChange={(e) => setFeePayer(e.target.value)}
                                className="w-full p-3 border border-gray-300 rounded-lg bg-gray-200 text-black h-12"
                            >
                                <option value="SENDER">Sender</option>
                                <option value="RECEIVER">Receiver</option>
                            </select>
                        </div>
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 font-medium mb-2">Message</label>
                        <textarea
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder="Enter message"
                            className="w-full p-3 border border-gray-300 rounded-lg"
                        ></textarea>
                    </div>

                    <button
                        onClick={handleTransaction}
                        disabled={loading}
                        className={`w-full py-3 font-semibold rounded-lg ${
                            loading
                                ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                : 'bg-blue-500 text-white hover:bg-blue-600'
                        }`}
                    >
                        {loading ? 'Submitting...' : 'Submit'}
                    </button>
                </div>
            </div>

            {/* OTP Modal */}
            {otpSent && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-lg font-bold mb-4">OTP Verification</h3>
                        <input
                            type="text"
                            value={otp}
                            onChange={(e) => setOtp(e.target.value)}
                            placeholder="Enter OTP"
                            className="w-full p-3 border border-gray-300 rounded-lg mb-4"
                        />
                        <div className="flex justify-end space-x-4">
                            <button
                                onClick={handleOtpVerification}
                                className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600"
                            >
                                Verify OTP
                            </button>
                            <button
                                onClick={() => setOtpSent(false)}
                                className="bg-gray-300 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-400"
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
            <ToastContainer />
        </div>
    );
};

export default TransactionForm;
