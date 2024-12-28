import React, { useState, useCallback } from 'react';
import { useForm, SubmitHandler, set } from 'react-hook-form';
import debounce from 'lodash.debounce';

function formatAccountNumber(accountNumber: string): string {
    const formattedAccountNumber = accountNumber.replace(/\d{3}(?=\d)/g, '$& ');
    return formattedAccountNumber;
}

const DepositPage = () => {
    type FormData = {
        identifier: string;
        depositAmount: number;
    };

    const [usernameSearchResult, setUsernameSearchResult] = useState<string | null>(null);
    const [accountSearchResult, setAccountSearchResult] = useState<string | null>(null);
    const [isDepositUsernameSuccess, setIsDepositSuccess] = useState<boolean | null>(null);
    const [isDepositAccountSuccess, setIsDepositAccountSuccess] = useState<boolean | null>(null);
    const [rawAccountNumber, setRawAccountNumber] = useState<string>('');
    const searchByUsername = async (username: string) => {
        console.log('Searching username:', username);
        try {
            const response = await fetch(`http://localhost:8888/api/accounts/username/${username}`, {
                method: 'GET',
                // Add any headers or body data if required
            });
            if (response.ok) {
                const data = await response.json();
                // Process the response data
                console.log('Data:', data);
                // capital ownerName 
                setUsernameSearchResult(`${data.data.accountNumber}`);
            } else {
                // Handle error response
                console.error('Error:', response.status);
                setUsernameSearchResult(null);
            }
        } catch (error) {
            // Handle network or other errors
            console.error('Error:', error);
        }
    };

    const searchByAccountNumber = async (accountNumber: string) => {
        console.log('Searching account number:', accountNumber);
        try {
            const response = await fetch(`http://localhost:8888/api/accounts/${accountNumber}`, {
                method: 'GET',
                // Add any headers or body data if required
            });
            if (response.ok) {
                const data = await response.json();
                // Process the response data
                console.log('Data:', data);
                // capital ownerName 
                setAccountSearchResult(`${data.data.ownerName}`.toUpperCase());
            } else {
                // Handle error response
                console.error('Error:', response.status);
                setAccountSearchResult(null);
            }
        } catch (error) {
            // Handle network or other errors
            console.error('Error:', error);
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
    
        // Perform deposit logic for username
        try {
            const response = await fetch(`http://localhost:8888/api/accounts/deposit`, {
                method: 'POST',
                // Add any headers or body data if required
                headers: {
                    'Content-Type': 'application/json',
                },
                // manually add the username and deposit amount to the body
                body: JSON.stringify({
                    username: data.identifier,
                    deposit_amount: data.depositAmount,
                }),
            });
            if (response.ok) {
                const data = await response.json();
                // Process the response data
                console.log('Data:', data);
                setIsDepositSuccess(true);
            } else {
                // Handle error response
                console.error('Error:', response);

            }
        } catch (error) {
            // Handle network or other errors
            console.error('Error:', error);
        }
    };

    const onSubmitAccount: SubmitHandler<FormData> = async (data) => {
        console.log('Deposit by Account Number:', data);
    
        // Perform deposit logic for username
        try {
            const response = await fetch(`http://localhost:8888/api/accounts/deposit`, {
                method: 'POST',
                // Add any headers or body data if required
                headers: {
                    'Content-Type': 'application/json',
                },
                // manually add the username and deposit amount to the body
                body: JSON.stringify({
                    account_number: data.identifier,
                    deposit_amount: data.depositAmount,
                }),
            });
            if (response.ok) {
                const data = await response.json();
                // Process the response data
                console.log('Data:', data);
                setIsDepositAccountSuccess(true);
            } else {
                // Handle error response
                console.error('Error:', response);

            }
        } catch (error) {
            // Handle network or other errors
            console.error('Error:', error);
        }

    };

    const handleAccountNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value.replace(/\s/g, ''); // Remove spaces for raw value
        if (/^\d*$/.test(rawValue)) { // Allow only numeric input
            setRawAccountNumber(rawValue);
            debouncedSearchByAccountNumber(rawValue); // Trigger search with raw value
        }
    };

    return (
        <div className="flex-row flex flex-grow w-[calc(100vw-256px)] max-w-full">
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
                                        onChange: (e) => debouncedSearchByUsername(e.target.value),
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
                            {isDepositUsernameSuccess && (
                                <p className="text-green-500 text-sm mt-2">Deposit successful!</p>
                            )} 
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
                            {isDepositAccountSuccess && (
                                <p className="text-green-500 text-sm mt-2">Deposit successful!</p>
                            )}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DepositPage;
