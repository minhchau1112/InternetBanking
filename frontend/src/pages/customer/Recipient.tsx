import { Edit, Delete, NavigateBefore, NavigateNext } from '@mui/icons-material';
import {useEffect, useState} from 'react';
import axios from "axios";

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

const Recipient = () => {

    const [recipients, setRecipients] = useState<Recipient[]>([]);
    const [filteredRecipients, setFilteredRecipients] = useState<Recipient[]>([]);
    const [pageRecipients, setPageRecipients] = useState<Recipient[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPage, setTotalPage] = useState(1);
    const [pageLimit] = useState(10);


    useEffect(() => {
        fetchRecipients();
    }, []);

    const fetchRecipients = async () => {
        try {
            const response = await axios.get("http://localhost:8888/api/recipients/12", {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
                },
            });

            const { data } = response.data;
            console.log(data.recipients);
            setRecipients(data.recipients);

            // Update search and pagination after setting recipients
            handleSearch('', data.recipients);
        } catch (error) {
            console.error("Error fetching recipients:", error);
        }
    };

    const handleSearch = (query: string, data: Recipient[] = recipients) => {
        setSearchQuery(query);

        let results;
        if (query.trim() === '') {
            // If the query is empty, show all recipients
            console.log("AAAA");
            results = data;
            console.log(results);
        } else {
            const lowercasedQuery = query.toLowerCase();
            results = data.filter(
                (recipient) =>
                    recipient.aliasName.toLowerCase().includes(lowercasedQuery) ||
                    recipient.accountNumber.includes(lowercasedQuery)
            );
        }

        setFilteredRecipients(results);

        // Update pagination based on the new filtered results
        const total = Math.max(1, Math.ceil(results.length / pageLimit));
        setTotalPage(total);

        // Update displayed recipients
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
                                            <button className="bg-transparent p-2"><Edit style={{color: "green"}}/>
                                            </button>
                                            <button className="bg-transparent p-2"><Delete style={{color: "red"}}/>
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
                </div>
            </div>
        </div>
    )
}

export default Recipient;