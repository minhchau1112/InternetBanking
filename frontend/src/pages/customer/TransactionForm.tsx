import {useEffect, useState} from 'react';
import axios from 'axios';
import {toast, ToastContainer} from "react-toastify";
import {createRecipient, fetchCustomerRecipients} from "@/api/recipientAPI.ts";
import Recipient from "@/pages/customer/Contact.tsx";
import NoDataImage from "@/assets/image/nodata.png";

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
    const [recipients, setRecipients] = useState<Recipient[]>([]);
    const [fee, setFee] = useState('');
    const [feePayer, setFeePayer] = useState('SENDER');
    const [accountOwner, setAccountOwner] = useState('');
    const [sourceAccountNumber, setSourceAccountNumber] = useState('');

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
                setSourceAccountNumber(accountResponse.data.data.accountNumber);

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

    useEffect(() => {
        if(activeTab === 'internal'){
            const delayDebounceFn = setTimeout(async () => {
                if (destinationAccount) {
                    try {
                        const response = await axios.get(`http://localhost:8888/api/accounts/${destinationAccount}`, {
                            headers: { Authorization: `Bearer ${accessToken}` },
                        });
                        console.log("account owner"+JSON.stringify(response.data, null, 2));
                        setAccountOwner(response.data.data.ownerName);
                    } catch (error) {
                        console.error('Error fetching account owner:', error);
                        setAccountOwner('');
                    }
                } else {
                    setAccountOwner('');
                }
            }, 500); // Delay of 500ms

            return () => clearTimeout(delayDebounceFn);
        }
        else if(activeTab === 'external'){
            const delayDebounceFn = setTimeout(async () => {
                if (destinationAccount) {
                    try {
                        const response = await axios.post(`http://localhost:8888/api/interbank/get-account-info/`, 
                        {
                            account_number: destinationAccount
                        }, 
                        {
                            headers: { Authorization: `Bearer ${accessToken}` }
                        });
                        console.log("account owner"+JSON.stringify(response.data, null, 2));
                        setAccountOwner(response.data.data.lastName + " " + response.data.data.firstName);
                    } catch (error) {
                        console.error('Error fetching account owner:', error);
                        setAccountOwner('');
                    }
                } else {
                    setAccountOwner('');
                }
            }, 500); // Delay of 500ms

            return () => clearTimeout(delayDebounceFn);
        }
    }, [destinationAccount, accessToken]);

    const handleTransaction = async () => {
        if(activeTab === 'internal'){
            setLoading(true);
            try {
                const destinationAccountNumber = recipientOption === 'choose' ? selectedRecipient : destinationAccount;

                const response = await axios.post(
                    'http://localhost:8888/api/transactions/create',
                    {
                        sourceAccountId: selectedAccount,
                        destinationAccountNumber: destinationAccountNumber,
                        amount,
                        fee,
                        feePayer,
                        type:"TRANSFER",
                        message,
                    },
                    {
                        headers: { Authorization: `Bearer ${accessToken}` },
                    }
                );
                console.log("transaction response"+JSON.stringify(response.data, null, 2));
                setTransactionId(response.data.data.transactionId);
                setOtpSent(true);
            } catch (error) {
                toast.error('Error creating transaction.');
            } finally {
                setLoading(false);
            }
        }
        else if(activeTab === 'external'){
            setLoading(true);
            try {
                const destinationAccountNumber = recipientOption === 'choose' ? selectedRecipient : destinationAccount;

                const response = await axios.post(
                    'http://localhost:8888/api/interbank/create',
                    {
                        sender_account_number: sourceAccountNumber,
                        sender_bank_code: "GROUP2",
                        recipient_account_number: destinationAccountNumber,
                        amount: amount,
                        transaction_type: "interbank",
                        fee_payer: feePayer.toLowerCase(),
                        fee_amount: fee,
                        description: message,
                        status: "pending"
                    },
                    {
                        headers: { Authorization: `Bearer ${accessToken}` },
                    }
                );
                console.log("transaction response"+JSON.stringify(response.data, null, 2));
                setTransactionId(response.data.data.payload);
                setOtpSent(true);
            } catch (error) {
                toast.error('Error creating transaction.');
            } finally {
                setLoading(false);
            }
        }
    };

    useEffect(() => {
        if (amount) {
            const calculatedFee = (parseFloat(amount) * 0.05).toFixed(2); // Fee is 5% of the amount
            setFee(calculatedFee); // Update the fee state
        }
    }, [amount]);

    const handleOtpVerification = async () => {
        if(activeTab === 'internal'){
            try {
                await axios.post(
                    'http://localhost:8888/api/transactions/verify-otp',
                    { transactionId, otp },
                    { headers: { Authorization: `Bearer ${accessToken}` } }
                );
                toast.success('Transaction completed successfully!');

                //Lưu người nhận mới
            if(!recipients.some(recipient => recipient.accountNumber === destinationAccount)){
                const reicipientResponse = await createRecipient(destinationAccount, "", "GROUP2");
                toast.success(reicipientResponse);
            }

                // Trì hoãn reload để người dùng kịp thấy thông báo
            setTimeout(() => {
                // Reload lại trang sau 2 giây
                setAmount('');
                setFee('');
                setFeePayer('SENDER');
                setType('TRANSFER');
                setMessage('');
                setOtp('');
                setTransactionId(null);
                setOtpSent(false);
                fetchRecipients();
            }, 3000);
            } catch (error) {
                toast.error('Invalid or expired OTP.');
            }
        }
        else if(activeTab === 'external'){
            try {
                await axios.post(
                    'http://localhost:8888/api/interbank/verify-otp',
                    { transactionId, otp },
                    { headers: { Authorization: `Bearer ${accessToken}` } }
                );
        
                // Call the transfer API after OTP verification
                const destinationAccountNumber = recipientOption === 'choose' ? selectedRecipient : destinationAccount;
                await axios.post(
                    'http://localhost:8888/api/interbank/transfer',
                    {
                        sender_account_number: sourceAccountNumber,
                        sender_bank_code: "GROUP2",
                        recipient_account_number: destinationAccountNumber,
                        amount: amount,
                        transaction_type: "interbank",
                        fee_payer: feePayer.toLowerCase(),
                        fee_amount: fee,
                        description: message,
                        status: "pending"
                    },
                    {
                        headers: { Authorization: `Bearer ${accessToken}` },
                    }
                );
        
                toast.success('Transaction completed successfully!');
                setTimeout(() => window.location.reload(), 3000);
            } catch (error) {
                toast.error('Invalid or expired OTP or transfer failed.');
            }
        }
    };

    const fetchRecipients = async () => {
        try {
            const response = await fetchCustomerRecipients();
            setRecipients(response);
        } catch (error) {
            toast.error("Error fetching recipients.");
        }
    };

    useEffect(() => {
        fetchRecipients();
    }, []);


    return (
        // <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
        //  <div className="bg-white shadow-lg rounded-lg p-8 w-full max-w-3xl grid grid-cols-3">
        //         <div className="relative col-span-2">
        //             <h2 className="text-2xl font-semibold text-gray-700 mb-6">Transaction Form</h2>
        //             <div className="space-y-4">
        //                 <div>
        //                     <label className="block text-gray-700 font-medium mb-2">Destination Account Number</label>
        //                     <input
        //                         type="text"
        //                         placeholder="Enter destination account"
        //                         value={destinationAccount}
        //                         onChange={(e) => setDestinationAccount(e.target.value)}
        //                         className="w-full p-3 border border-gray-300 rounded-lg bg-gray-800 text-white placeholder-gray-400"
        //                     />
        <div className="w-full h-full max-h-screen flex flex-col bg-gray-100 pt-7 px-5">
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
            <div className="flex-grow py-6 w-full h-full max-h-screen overflow-auto">
                <div className="max-w-full mx-auto bg-white shadow-md rounded-lg px-6 py-2">
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
                        <div className="flex space-x-4">
                            <div className='w-full'>
                                <label className="block text-gray-700 font-medium mb-2">Destination Account Number</label>
                                <input
                                    type="text"
                                    value={destinationAccount}
                                    onChange={(e) => setDestinationAccount(e.target.value)}
                                    placeholder="Enter destination account"
                                    className="w-full p-3 border border-gray-300 rounded-lg"
                                />
                            </div>
                            <div className='w-full'>
                                <label className="block text-gray-700 font-medium mb-2">Account's Owner</label>
                                <input
                                    type="text"
                                    value={accountOwner}
                                    onChange={(e) => setDestinationAccount(e.target.value)}
                                    placeholder="Account's Owner"
                                    className="w-full p-3 border border-gray-300 rounded-lg bg-gray-200"
                                    disabled
                                    readOnly
                                />
                            </div>
                        </div>
                    )}

                    {/* Amount and Message */}
                    <div className="flex space-x-4">
                        <div className="w-full">
                            <label className="block text-gray-700 font-medium mb-2">Amount</label>
                            <input
                                type="number"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                                placeholder="Enter amount"
                                className="w-full p-3 border border-gray-300 rounded-lg"
                            />
                        </div>
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
                    <div className='flex flex-row justify-center'>
                        <button
                            onClick={handleTransaction}
                            disabled={loading}
                            className={`w-full max-w-40 py-3 font-semibold rounded-lg ${
                                loading
                                    ? 'bg-gray-400 text-gray-600 cursor-not-allowed'
                                    : 'bg-blue-500 text-white hover:bg-blue-600'
                            }`}
                        >
                            {loading ? 'Submitting...' : 'Submit'}
                        </button>
                    </div>

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
            //         <button
            //             onClick={handleTransaction}
            //             disabled={loading} // Disable khi đang tải
            //             className={`py-2 px-6 rounded-lg font-semibold ${
            //                 loading
            //                     ? 'bg-gray-500 text-gray-300 cursor-not-allowed'
            //                     : 'bg-blue-500 text-white hover:bg-blue-600'
            //             }`}
            //         >
            //             {loading ? 'Submitting...' : 'Submit'}
            //         </button>

            //         {otpSent && (
            //             <div
            //                 className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm">
            //                 <div className="bg-white p-6 rounded-lg shadow-lg w-[90%] max-w-md">
            //                     <h3 className="text-lg font-bold mb-4">OTP Verification</h3>
            //                     <input
            //                         type="text"
            //                         placeholder="Enter OTP"
            //                         value={otp}
            //                         onChange={(e) => setOtp(e.target.value)}
            //                         className="w-full p-3 border border-gray-300 rounded-lg mb-4 text-white"
            //                     />
            //                     <div className="flex justify-end space-x-4">
            //                         <button
            //                             onClick={handleOtpVerification}
            //                             className="bg-blue-500 text-white font-semibold py-2 px-6 rounded-lg hover:bg-blue-600"
            //                         >
            //                             Verify OTP
            //                         </button>
            //                         <button
            //                             onClick={() => setOtpSent(false)}
            //                             className="bg-gray-300 text-gray-700 font-semibold py-2 px-6 rounded-lg hover:bg-gray-400"
            //                         >
            //                             Cancel
            //                         </button>
            //                     </div>
            //                 </div>
            //             </div>
            //         )}
            //     </div>


            //     <div className="w-full h-full col-span-1 border-l-[1px] border-gray-200 ml-8 flex flex-col">
            //         <h1 className="text-xl font-semibold text-gray-700 pl-4 mb-2">Contact</h1>
            //         {recipients.length > 0 ? (
            //             <div className="w-full max-h-[525px] flex-1 flex flex-col overflow-auto overflow-y-scroll">
            //                 {recipients.map((recipient) => (
            //                     <div key={recipient.id} onClick={() => setDestinationAccount(recipient.accountNumber)}
            //                             className="w-full flex flex-col justify-center hover:bg-amber-200 pl-4 py-2 space-y-1">
            //                         <p className="text-md font-bold">{recipient.aliasName}</p>
            //                         <p className="text-sm font-light text-gray-400">{recipient.accountNumber}</p>
            //                     </div>
            //                 ))}
            //             </div>
            //         ) : (
            //             <div className="flex flex-col items-center mt-4">
            //                 <img src={NoDataImage} alt="No data" className="w-1/3" />
            //                 <p className="text-gray-500 mt-2">
            //                     You don't have any contact yet.
            //                 </p>
            //             </div>
            //         )}
            //     </div>
            //     <ToastContainer/>
            // </div>
                </div>
            )}
            <ToastContainer />
        </div>
    );
};

export default TransactionForm;
