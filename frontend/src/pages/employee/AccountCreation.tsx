import React, { useState } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import {toast, ToastContainer} from "react-toastify";
import accountImage from '../../assets/image/account.png';

type FormData = {
    username: string;
    email: string;
    name: string;
    phone: string;
};

type AccountInfo = {
    username: string;
    password: string;
};

const AccountCreation = () => {
    const { register, handleSubmit, formState: { errors } } = useForm<FormData>();
    const [accountInfo, setAccountInfo] = useState<AccountInfo | null>(null);
    const [errorMessage, setErrorMessage] = useState<string>('');

    const onSubmit: SubmitHandler<FormData> = (data) => {
        console.log(data);
        // Perform account creation logic here
        try {
            fetch('http://localhost:8888/api/accounts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                },
                body: JSON.stringify({
                    username: data.username,
                    name: data.name,
                    email: data.email,
                    phone: data.phone,
                    role: 'customer'
                }),
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    setErrorMessage(data.error);
                    setAccountInfo(null);
                    toast.error(data.error);
                } else {
                    setAccountInfo(data.data);
                    setErrorMessage('');  // Clear error if account creation is successful
                    // alert('Account created successfully');
                    toast.success("Account created successfully.");
                    console.log('Success:', data);
                }
            });
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div className="w-[calc(100vw-256px)] max-w-full h-screen flex items-center justify-center p-8 bg-gray-100 ">
            <div className="w-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-3xl font-bold mb-6 text-gray-800 text-left">Create Customer Account</h1>
                <div className="w-full grid grid-cols-2">
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 w-full">
                        <div>
                            <label className="block text-gray-700 text-lg mb-2">Username</label>
                            <div className="flex items-center">
                                <input
                                    {...register('username', { required: "Username is required" })}
                                    className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                                />
                                <div className="text-red-500 ml-4 text-sm">
                                    {errors.username?.message && String(errors.username.message)}
                                </div>
                            </div>
                        </div>

                        <div>
                            <label className="block text-gray-700 text-lg mb-2">Email</label>
                            <div className="flex items-center">
                                <input
                                    {...register('email', { 
                                        required: "Email is required", 
                                        pattern: { 
                                            value: /^\S+@\S+$/i, 
                                            message: "Invalid email address" 
                                        } 
                                    })}
                                    className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                                />
                                <div className="text-red-500 ml-4 text-sm">
                                    {errors.email?.message && String(errors.email.message)}
                                </div>
                            </div>
                        </div>

                        <div>
                            <label className="block text-gray-700 text-lg mb-2">Name</label>
                            <div className="flex items-center">
                                <input
                                    {...register('name', { required: "Name is required" })}
                                    className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                                />
                                <div className="text-red-500 ml-4 text-sm">
                                    {errors.name?.message && String(errors.name.message)}
                                </div>
                            </div>
                        </div>

                        <div>
                            <label className="block text-gray-700 text-lg mb-2">Phone</label>
                            <div className="flex items-center">
                                <input
                                    {...register('phone', { 
                                        required: "Phone is required", 
                                        pattern: { 
                                            value: /^[0-9]{10}$/, 
                                            message: "Invalid phone number, must be 10 digits" 
                                        }
                                    })}
                                    className="border border-gray-300 rounded-lg px-4 py-3 w-full text-lg"
                                />
                                <div className="text-red-500 ml-4 text-sm">
                                    {errors.phone?.message && String(errors.phone.message)}
                                </div>
                            </div>
                        </div>

                        <div className="text-center">
                            <button
                                type="submit"
                                className="bg-blue-600 hover:bg-blue-700 text-white font-bold px-6 py-3 rounded-lg text-lg"
                            >
                                Create Account
                            </button>
                        </div>
                    </form>
                    {/* Account Info / Error Display */}
                    <div className="space-y-6 flex flex-col justify-center items-center p-6 rounded-lg">
                        <div className="w-full">
                            {/* Success Message */}
                            {!accountInfo && !errorMessage && (
                                <div className="flex-grow w-full p-4 rounded-lg mb-4">
                                    {/* // image from assets/image/account.png */}
                                    <img src={accountImage} alt="Account created" className="w-full h-full mx-auto"/>
                                </div>
                            )}
                            {accountInfo && !errorMessage && (
                                <div className="bg-green-100 p-4 rounded-lg shadow-md mb-4">
                                    <p className="text-green-600 text-center font-semibold">Account created successfully!</p>
                                </div>
                            )}
                            {accountInfo ? (
                                <div className="flex flex-col justify-start space-y-4">
                                    <h2 className="text-2xl font-semibold text-gray-800 text-center">Account Information</h2>
                                    <div className="bg-white p-6 rounded-lg shadow-md">
                                        <div className="space-y-4">
                                            <p className="text-gray-700"><strong>Username:</strong> {accountInfo.username}</p>
                                            <p className="text-gray-700"><strong>Password:</strong> {accountInfo.password}</p>
                                        </div>
                                    </div>
                                </div>
                            ) : (
                                errorMessage && (
                                    <div className="bg-red-100 p-4 rounded-lg shadow-md">
                                        <p className="text-red-500 text-center font-semibold">{errorMessage}</p>
                                    </div>
                                )
                            )}
                        </div>
                    </div>
                </div>
            </div>
            <ToastContainer/>
        </div>
    );
};

export default AccountCreation;
