import { Edit, Delete, NavigateBefore, NavigateNext } from '@mui/icons-material';
import {useEffect, useState} from 'react';
import {useForm, SubmitHandler} from "react-hook-form";
import {toast, ToastContainer} from "react-toastify";
import {createRecipient, deleteRecipients, fetchCustomerRecipients, updateRecipient} from "@/api/recipientAPI.ts";

type Customer = {
    createdAt: string,
    email: string,
    id: number,
    name: string,
    password: string,
    phone: string,
    username: string,
}

type Recipient = {
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

    const [recipients, setRecipients] = useState<Recipient[]>([]);
    const [filteredRecipients, setFilteredRecipients] = useState<Recipient[]>([]);
    const [pageRecipients, setPageRecipients] = useState<Recipient[]>([]);
    const [searchQuery, setSearchQuery] = useState<string>('');
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [totalPage, setTotalPage] = useState<number>(1);
    const [pageLimit] = useState<number>(10);
    const { register: resC, handleSubmit: subC, formState: { errors: errC }, reset: resetC } = useForm<FormData>();
    const { register: resU, handleSubmit: subU, formState: { errors: errU }, reset: resetU, setValue: setU } = useForm<FormData>();
    const [updatingRecipientId, setUpdatingRecipientId] = useState<number>(-1);
    useEffect(() => {
        fetchRecipients();
    }, []);

    const fetchRecipients = async () => {
        try {
            const response = await fetchCustomerRecipients();
            setRecipients(response);
            handleSearch('', response);
        } catch (error) {
            toast.error("Error fetching recipients.");
        }
    };

    const handleSearch = (query: string, data: Recipient[] = recipients) => {
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

    const handleDisplayRecipients = (data: Recipient[] = filteredRecipients, page: number = currentPage) => {
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

    const handleDelete = async (recipientId: number) => {
        resetU();
        setUpdatingRecipientId(-1);
        try {
            const response = await deleteRecipients(recipientId);
            toast.success(response);
            fetchRecipients();
        } catch (err) {
            toast.error("Error delete recipient");
        }
    }

    const onSubmitCreate: SubmitHandler<FormData> = async (data) => {
        try {
            const response = await createRecipient(data.accountNumber, data.aliasName, data.bankCode);
            toast.success(response);
            resetC();
            fetchRecipients();
        } catch (err) {
            toast.error("Error create recipient");
        }
    };

    const parseUpdateForm = (data: Recipient) => {
        setU("bankCode", data.bankCode);
        setU("accountNumber", data.accountNumber);
        setU("aliasName", data.aliasName);
        setUpdatingRecipientId(data.id);
    }
    const onSubmitUpdate: SubmitHandler<FormData> = async (data) => {
        try {
            const response = await updateRecipient(
                updatingRecipientId,
                data.accountNumber,
                data.aliasName,
                data.bankCode
            )

            toast.success(response);
            resetU();
            setUpdatingRecipientId(-1);
            fetchRecipients();
        } catch (err) {
            toast.error("Error update recipient");
        }
    };
    return (
        <div className="w-[calc(100vw-256px)] max-w-full h-screen flex items-center justify-center p-8 bg-gray-100 ">
            <div className="w-full h-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-3xl font-bold mb-3 text-gray-800 text-left">Manage Customer Recipients</h1>
                <input
                    className="border border-gray-300 rounded-lg px-[1%] py-1 mb-[1%] w-[20%] text-lg"
                    type="text"
                    placeholder="Search..."
                    value={searchQuery}
                    onChange={(e) => handleSearch(e.target.value)}
                />
                <div className="w-full grid grid-cols-6">
                    <div className="w-full h-full flex flex-col justify-start items-start col-span-4">
                        <div className="w-full h-fit flex flex-col justify-start px-8 py-4 border border-gray-300 rounded-lg">
                            <table className="w-full text-lg text-left pb-[1%] border-collapse">
                                <thead>
                                <tr className="border-b border-gray-300">
                                    <th className="py-2 w-[40%] font-bold">Alias Name</th>
                                    <th className="py-2 w-[30%] font-bold">Account Number</th>
                                    <th className="py-2 w-[20%] font-bold">Bank Code</th>
                                    <th className="py-2 w-[10%]"></th>
                                </tr>
                                </thead>
                                <tbody>
                                {pageRecipients.map((recipient) => (
                                    <tr key={recipient.id}>
                                        <td className="py-2">{recipient.aliasName}</td>
                                        <td className="py-2 text-gray-400">{recipient.accountNumber}</td>
                                        <td className="py-2">{recipient.bankCode}</td>
                                        <td className="py-2 flex">
                                            <button className="bg-transparent p-2"
                                                    onClick={() => parseUpdateForm(recipient)}>
                                                <Edit style={{color: "green"}}/>
                                            </button>
                                            <button className="bg-transparent p-2"
                                                    onClick={() => handleDelete(recipient.id)}>
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

                    <div className="w-full h-full flex flex-col justify-between items-start col-span-2 space-y-4">
                        <div className="w-full px-8 py-4 ml-4 border border-gray-300 rounded-lg">
                            <h1 className="w-full text-2xl font-bold text-gray-800 mb-4 text-center">
                                New Contact</h1>
                            <form onSubmit={subC(onSubmitCreate)} className="space-y-4 w-full">
                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Bank Code</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...resC('bankCode', {required: "Bank Code is required"})}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errC.bankCode?.message && String(errC.bankCode.message)}
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Account Number</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...resC('accountNumber', {
                                                required: "Account Number is required",
                                            })}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errC.accountNumber?.message && String(errC.accountNumber.message)}
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Alias Name</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...resC('aliasName')}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errC.aliasName?.message && String(errC.aliasName.message)}
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

                        <div className="w-full h-full px-8 py-4 ml-4 border border-gray-300 rounded-lg">
                            <h1 className="w-full text-2xl font-bold text-gray-800 mb-4 text-center">
                                Edit Contact Profile</h1>
                            <form onSubmit={subU(onSubmitUpdate)} className="space-y-4 w-full">
                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Bank Code</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...resU('bankCode', {required: "Bank Code is required"})}
                                            disabled={true}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg disabled:bg-gray-300"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errU.bankCode?.message && String(errU.bankCode.message)}
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Account Number</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...resU('accountNumber', {
                                                required: "Account Number is required",
                                            })}
                                            disabled={true}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg disabled:bg-gray-300"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errU.accountNumber?.message && String(errU.accountNumber.message)}
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-gray-700 text-md mb-1">Alias Name</label>
                                    <div className="flex flex-col">
                                        <input
                                            {...resU('aliasName')}
                                            className="border border-gray-300 rounded-lg px-2 py-1 w-full text-lg"
                                        />
                                        <div className="text-red-500 text-md">
                                            {errU.aliasName?.message && String(errU.aliasName.message)}
                                        </div>
                                    </div>
                                </div>

                                <div className="text-center">
                                    <button
                                        type="submit"
                                        className="bg-black hover:bg-blue-700 text-white px-3 py-1 rounded-lg text-lg"
                                    >
                                        Change
                                    </button>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
            <ToastContainer/>
        </div>
    )
}

export default Recipient;