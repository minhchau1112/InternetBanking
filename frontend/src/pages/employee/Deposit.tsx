import React, { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useForm, SubmitHandler } from 'react-hook-form';
import { toast, ToastContainer } from "react-toastify";
import debounce from 'lodash.debounce';
import { setUsernameSearchResult, setAccountSearchResult, setRawAccountNumber } from '../../redux/slices/searchDepositSlice';
import { setLoading, setSuccessMessage, setErrorMessage } from '../../redux/slices/depositSlice';

function formatAccountNumber(accountNumber: string): string {
    const formattedAccountNumber = accountNumber.replace(/\d{3}(?=\d)/g, '$& ');
    return formattedAccountNumber;
}

const DepositPage = () => {
    const dispatch = useDispatch();
    const { usernameSearchResult, accountSearchResult, rawAccountNumber } = useSelector((state: any) => state.search);
    // const { isLoading, successMessage, errorMessage } = useSelector((state: any) => state.deposit);

    type FormData = {
        identifier: string;
        depositAmount: number;
    };

    const searchByUsername = async (username: string) => {
        console.log('Searching username:', username);
        try {
            const response = await fetch(`http://localhost:8888/api/accounts/username/${username}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                }
            });
            if (response.ok) {
                const data = await response.json();
                console.log('Data:', data);
                dispatch(setUsernameSearchResult(`${data.data.accountNumber}`));
            } else {
                console.error('Error:', response.status);
                dispatch(setUsernameSearchResult(null));
            }
        } catch (error) {
            console.error('Error:', error);
            dispatch(setUsernameSearchResult(null));
        }
    };

    const searchByAccountNumber = async (accountNumber: string) => {
        console.log('Searching account number:', accountNumber);
        try {
            const response = await fetch(`http://localhost:8888/api/accounts/${accountNumber}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                }
            });
            if (response.ok) {
                const data = await response.json();
                console.log('Data:', data);
                dispatch(setAccountSearchResult(`${data.data.ownerName}`.toUpperCase()));
            } else {
                console.error('Error:', response.status);
                dispatch(setAccountSearchResult(null));
            }
        } catch (error) {
            console.error('Error:', error);
            dispatch(setAccountSearchResult(null));
        }
    };

    const debouncedSearchByUsername = useCallback(debounce(searchByUsername, 500), []);
    const debouncedSearchByAccountNumber = useCallback(debounce(searchByAccountNumber, 500), []);

    const {
        register: registerUsername,
        handleSubmit: handleSubmitUsername,
        formState: { errors: usernameErrors },
    } = useForm<FormData>();

    const {
        register: registerAccount,
        handleSubmit: handleSubmitAccount,
        formState: { errors: accountErrors },
    } = useForm<FormData>();

    const onSubmitUsername: SubmitHandler<FormData> = async (data) => {
        console.log('Deposit by Username:', data);
        dispatch(setLoading(true));

        try {
            const response = await fetch(`http://localhost:8888/api/accounts/deposit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                },
                body: JSON.stringify({
                    username: data.identifier,
                    deposit_amount: data.depositAmount,
                }),
            });
            if (response.ok) {
                const data = await response.json();
                console.log('Data:', data);
                dispatch(setSuccessMessage('Deposit successful.'));
                toast.success("Deposit successful.");
            } else {
                console.error('Error:', response);
                dispatch(setErrorMessage('Deposit failed.'));
                toast.error("Deposit failed.");
            }
        } catch (error) {
            console.error('Error:', error);
            dispatch(setErrorMessage('Deposit failed.'));
        } finally {
            dispatch(setLoading(false));
        }
    };

    const onSubmitAccount: SubmitHandler<FormData> = async (data) => {
        console.log('Deposit by Account Number:', data);
        dispatch(setLoading(true));

        try {
            const accountNumber = data.identifier.replace(/\s+/g, '');
            
            const response = await fetch(`http://localhost:8888/api/accounts/deposit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                },
                body: JSON.stringify({
                    account_number: accountNumber,
                    deposit_amount: data.depositAmount,
                }),
            });
            if (response.ok) {
                const data = await response.json();
                console.log('Data:', data);
                dispatch(setSuccessMessage('Deposit successful.'));
                toast.success("Deposit successful.");
            } else {
                console.error('Error:', response);
                dispatch(setErrorMessage('Deposit failed.'));
                toast.error("Deposit failed.");
            }
        } catch (error) {
            console.error('Error:', error);
            dispatch(setErrorMessage('Deposit failed.'));
        } finally {
            dispatch(setLoading(false));
        }
    };

    const handleAccountNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value.replace(/\s/g, '');
        if (/^\d*$/.test(rawValue)) {
            dispatch(setRawAccountNumber(rawValue));
            debouncedSearchByAccountNumber(rawValue);
        }
        if (rawValue === '') {
            dispatch(setAccountSearchResult(null)); // Reset owner name when account number input is empty
        }
    };

    const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const username = e.target.value;
        if (username === '') {
            dispatch(setUsernameSearchResult(null)); // Reset account number when username input is empty
        }
        debouncedSearchByUsername(username);
    };

    return (
        <div className="flex-row flex flex-grow w-[calc(100vw-300px)] max-w-full">
            <div className="w-full h-screen bg-gray-100 flex flex-col items-center py-8 pl-12">
                <h1 className="text-3xl font-bold mb-6 text-gray-800">Deposit Page</h1>
                <div className="w-full grid grid-cols-1 md:grid-cols-2 gap-8">
                    {/* Search by Username */}
                    <div className="bg-white p-6 rounded-lg shadow-md min-w-[500px]">
                        <h2 className="text-2xl font-semibold mb-4 text-gray-700">Search by Username</h2>
                        <form onSubmit={handleSubmitUsername(onSubmitUsername)} className="space-y-4">
                            <div>
                                <label className="block text-gray-700 mb-2">Username</label>
                                <input
                                    {...registerUsername('identifier', { 
                                        required: 'Username is required',
                                        onChange: handleUsernameChange,
                                    })}
                                    className="border border-gray-300 rounded-lg px-4 py-2 w-full"
                                    placeholder="Enter username"
                                />
                                {usernameErrors.identifier && (
                                    <p className="text-red-500 text-sm mt-1">{usernameErrors.identifier.message}</p>
                                )}
                            </div>
                            <div>
                                <label className="block text-gray-700 mb-2">Linked Account Number</label>
                                <input
                                    value={usernameSearchResult ? formatAccountNumber(usernameSearchResult) : ''}
                                    readOnly
                                    className="border border-gray-300 rounded-lg px-4 py-2 w-full bg-gray-100"
                                    placeholder="Account number will appear here"
                                />
                            </div>
                            <div>
                                <label className="block text-gray-700 mb-2">Deposit Amount</label>
                                <input
                                    {...registerUsername('depositAmount', {
                                        required: 'Deposit amount is required',
                                        valueAsNumber: true,
                                        min: { value: 1, message: 'Amount must be at least 1' },
                                    })}
                                    type="number"
                                    className="border border-gray-300 rounded-lg px-4 py-2 w-full"
                                    placeholder="Enter deposit amount"
                                />
                                {usernameErrors.depositAmount && (
                                    <p className="text-red-500 text-sm mt-1">{usernameErrors.depositAmount.message}</p>
                                )}
                            </div>
                            <button
                                type="submit"
                                className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg"
                            >
                                Deposit
                            </button>
                        </form>
                    </div>

                    {/* Search by Account Number */}
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h2 className="text-2xl font-semibold mb-4 text-gray-700">Search by Account Number</h2>
                        <form onSubmit={handleSubmitAccount(onSubmitAccount)} className="space-y-4">
                            <div>
                                <label className="block text-gray-700 mb-2">Account Number</label>
                                <input
                                    {...registerAccount('identifier', { 
                                        required: 'Account number is required',
                                    })}
                                    value={formatAccountNumber(rawAccountNumber)}
                                    onChange={handleAccountNumberChange}
                                    className="border border-gray-300 rounded-lg px-4 py-2 w-full"
                                    placeholder="Enter account number"
                                />
                                {accountErrors.identifier && (
                                    <p className="text-red-500 text-sm mt-1">{accountErrors.identifier.message}</p>
                                )}
                            </div>
                            <div>
                                <label className="block text-gray-700 mb-2">Owner Name</label>
                                <input
                                    value={accountSearchResult || ''}
                                    readOnly
                                    className="border border-gray-300 rounded-lg px-4 py-2 w-full bg-gray-100"
                                    placeholder="Owner name will appear here"
                                />
                            </div>
                            <div>
                                <label className="block text-gray-700 mb-2">Deposit Amount</label>
                                <input
                                    {...registerAccount('depositAmount', {
                                        required: 'Deposit amount is required',
                                        valueAsNumber: true,
                                        min: { value: 1, message: 'Amount must be at least 1' },
                                    })}
                                    type="number"
                                    className="border border-gray-300 rounded-lg px-4 py-2 w-full"
                                    placeholder="Enter deposit amount"
                                />
                                {accountErrors.depositAmount && (
                                    <p className="text-red-500 text-sm mt-1">{accountErrors.depositAmount.message}</p>
                                )}
                            </div>
                            <button
                                type="submit"
                                className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg"
                            >
                                Deposit
                            </button>
                        </form>
                    </div>
                </div>
            </div>
            <ToastContainer/>
        </div>
    );
};

export default DepositPage;
