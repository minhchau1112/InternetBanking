import { Edit, Delete, NavigateBefore, NavigateNext } from '@mui/icons-material';
import {useEffect, useState} from 'react';
import axios from "axios";
import {useForm, SubmitHandler} from "react-hook-form";

type Customer = {
    createdAt: string,
    email: string,
    id: number,
    name: string,
    password: string,
    phone: string,
    username: string,
}

type Contact = {
    id: number,
    customer: Customer,
    accountNumber: string,
    aliasName: string,
    bankCode: string,
}

type FormData = {
    accountNumber: string,
    aliasName: string,
    bankCode: string
}

const Recipient = () => {

    const [userAccountId] = useState<string>(localStorage.getItem('accountId') || '')
    const [recipients, setRecipients] = useState<Contact[]>([]);
    const [filteredRecipients, setFilteredRecipients] = useState<Contact[]>([]);
    const [pageRecipients, setPageRecipients] = useState<Contact[]>([]);
    const [searchQuery, setSearchQuery] = useState<string>('');
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [totalPage, setTotalPage] = useState<number>(1);
    const [pageLimit] = useState<number>(10);
    const { register, handleSubmit, formState: { errors } } = useForm<FormData>();

    useEffect(() => {
        fetchRecipients();
    }, []);

    const fetchRecipients = async () => {
        try {
            const response = await axios.get(`http://localhost:8888/api/recipients/${userAccountId}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
                },
            });

            const { data } = response.data;
            setRecipients(data);
            handleSearch('', data);
        } catch (error) {
            console.error("Error fetching recipients:", error);
        }
    };

    const handleSearch = (query: string, data: Contact[] = recipients) => {
        setSearchQuery(query);

        let results;
        if (query.trim() === '') {
            results = data;
        } else {
            const lowercasedQuery = query.toLowerCase();
            results = data.filter(
                (recipient) =>
                    recipient.aliasName.toLowerCase().includes(lowercasedQuery) ||
                    recipient.accountNumber.includes(lowercasedQuery)
            );
        }

        setFilteredRecipients(results);
        const total = Math.max(1, Math.ceil(results.length / pageLimit));
        setTotalPage(total);
        handleDisplayRecipients(results);
    };

    const handleDisplayRecipients = (data: Contact[] = filteredRecipients, page: number = currentPage) => {
        const startIndex = (page - 1) * pageLimit;
        const currents = data.slice(startIndex, startIndex + pageLimit);
        setPageRecipients(currents);
    };

    const handleNextPage = () => {
        const nextPage = currentPage < totalPage ? currentPage + 1 : totalPage;
        handleDisplayRecipients(filteredRecipients, nextPage);
        setCurrentPage(nextPage);
    };

    const handlePrevPage = () => {
        const prevPage = currentPage > 1 ? currentPage - 1 : 1;
        handleDisplayRecipients(filteredRecipients, prevPage);
        setCurrentPage(prevPage);
    };

    const handleDelete = (customerId: number) => {
        try {
            const response = axios.delete(`http://localhost:8888/api/recipients/${customerId}`,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
                    },
                }
            );
            console.log(response);
            fetchRecipients();
        } catch (err) {
            console.log("failed");
        }
    }
    const onSubmit: SubmitHandler<FormData> = (data) => {
        try {
            const response = axios.post(`http://localhost:8888/api/recipients`,
                {
                    customerId: userAccountId,
                    accountNumber: data.accountNumber,
                    aliasName: data.aliasName,
                    bankCode: data.bankCode,
                },
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
                    },
                }
                );
            console.log(response);
            fetchRecipients();
        } catch (err) {
            console.log("failed");
        }
    };

    return (
        <div className="w-[calc(100vw-256px)] max-w-full h-screen flex items-center justify-center p-8 bg-gray-100 ">
            <div className="w-full h-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-3xl font-bold mb-3 text-gray-800 text-left">Manage Customer Recipients</h1>
                <div className="w-full grid grid-cols-5">
                    <div className="w-full h-full flex flex-col justify-center items-start col-span-3">
                        <input
                            className="border border-gray-300 rounded-lg px-[1%] py-1 mb-[1%] w-[40%] text-lg"
                            type="text"
                            placeholder="Search..."
                            value={searchQuery}
                            onChange={(e) => handleSearch(e.target.value)}
                        />
                        <div className="w-full px-[2%] pb-[2%] pt-[0.5%] border border-gray-300 rounded-3xl">
                            <table className="w-full text-lg text-left pb-[1%] border-collapse">
                                <thead>
                                <tr className="border-b border-gray-300">
                                    <th className="py-[2%] w-[40%] font-bold">Alias Name</th>
                                    <th className="py-[2%] w-[30%] font-bold">Account Number</th>
                                    <th className="py-[2%] w-[20%] font-bold">Bank Code</th>
                                    <th className="py-[2%] w-[10%]"></th>
                                </tr>
                                </thead>
                                <tbody>
                                {pageRecipients.map((recipient) => (
                                    <tr key={recipient.id}>
                                        <td className="py-[2%]">{recipient.aliasName}</td>
                                        <td className="py-[2%] text-gray-400">{recipient.accountNumber}</td>
                                        <td className="py-[2%]">{recipient.bankCode}</td>
                                        <td className="py-[2%] flex">
                                            <button className="bg-transparent p-2">
                                                <Edit style={{color: "green"}}/>
                                            </button>
                                            <button className="bg-transparent p-2" onClick={() => handleDelete(recipient.id)}>
                                                <Delete style={{color: "red"}}/>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                            <div className="flex justify-center items-center mt-[2%] space-x-[1.5%]">
                                <button disabled={(currentPage == 1)} onClick={() => handlePrevPage()}
                                        className="py-1 px-1 bg-black rounded-full disabled:opacity-50">
                                    <NavigateBefore style={{color: "white", fontSize: 30}}/></button>
                                <p className="text-lg">Page {currentPage}/{totalPage}</p>
                                <button disabled={(currentPage == totalPage)} onClick={() => handleNextPage()}
                                        className="py-1 px-1 bg-black rounded-full disabled:opacity-50">
                                    <NavigateNext style={{color: "white", fontSize: 30}}/></button>
                            </div>
                        </div>
                    </div>

                    <div className="w-full h-full flex flex-col justify-center items-start col-span-2">
                        <div className="w-full px-[2%] pb-[2%] pt-[2%] ml-[1%] border border-gray-300 rounded-3xl">
                            <h1 className="w-full text-2xl font-bold text-gray-800  mb-4 text-center">Create Customer Account</h1>
                            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 w-full">
                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Bank Code</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...register('bankCode', {required: "Bank Code is required"})}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errors.bankCode?.message && String(errors.bankCode.message)}
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Account Number</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...register('accountNumber', {
                                                required: "Account Number is required",
                                            })}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errors.accountNumber?.message && String(errors.accountNumber.message)}
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Alias Name</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...register('aliasName')}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errors.aliasName?.message && String(errors.aliasName.message)}
                                        </div>
                                    </div>
                                </div>

                                <div className="text-center">
                                    <button
                                        type="submit"
                                        className="bg-black hover:bg-blue-700 text-white px-3 py-1 rounded-lg text-lg"
                                    >
                                        Add Contact
                                    </button>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Recipient;