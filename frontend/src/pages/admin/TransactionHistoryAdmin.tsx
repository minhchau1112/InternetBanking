import { useEffect, useState } from 'react';
import NoDataImage from '@/assets/image/nodata.png';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '@/redux/store';
import { setTransactions, setFilteredTransactions, setActiveTab, setSortOrder, setError } from '@/redux/slices/transactionsSlice';
import axios from "axios";

type InterbankTransaction = {
    id: number;
    amount: number;
    date: string;
    description: string;
    accountNumber: string;
    bankName: string;
    tab: 'in' | 'out';
};

const TransactionHistoryAdmin = () => {
    const [sourceAccountId] = useState<string>(localStorage.getItem('accountId') || '');
    const [partnerAccountNumber, setPartnerAccountNumber] = useState<string>('');
    const [transactionType, setTransactionType] = useState<'internal' | 'interbank'>('internal');
    const [startDate, setStartDate] = useState<string>('');
    const [endDate, setEndDate] = useState<string>('');
    const [bankCode, setBankCode] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [banks, setBanks] = useState<Bank[]>([]);
    const [selectedBank, setSelectedBank] = useState<string>('');

    const accessToken = localStorage.getItem('access_token');

    const dispatch = useDispatch();
    const { allTransactions, filteredTransactions, activeTab, sortOrder } = useSelector((state: RootState) => state.transactions);

    useEffect(() => {
        const fetchBanks = async () => {
            try {
                const response = await axios.get('http://localhost:8888/api/banks/linked', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${accessToken}`,
                    },
                });
                setBanks(response.data.data);
                console.log("Banks: ", response.data.data);
            } catch (error) {
                console.error("Error fetching banks:", error);
            }
        };
        fetchBanks();
    }, []);

    // Hàm fetch dữ liệu giao dịch từ API
    const fetchTransactions = async () => {
        try {
            setError(null); // Clear previous errors
            const response = await axios.get("http://localhost:8888/api/transactions/interbank-transactions/filter", {
                method: "GET",
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                },
                params: new URLSearchParams({
                    startDate: startDate || '',
                    endDate: endDate || '',
                    bankCode: selectedBank || '',
                }),
            });
            console.log("Transactions: ", response.data.data);
            dispatch(setTransactions(response.data.data));
        } catch (error) {
            dispatch(setError("Failed to fetch transactions"));
        }
    };

    const handleBankChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedBank(e.target.value);
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters((prevFilters) => ({
            ...prevFilters,
            [name]: value,
        }));
    };

    // Hàm tìm kiếm
    const handleSearch = () => {
        fetchTransactions();
    };

    // Sort transactions
    const sortTransactions = (list: InterbankTransaction[]) => {
        return [...list].sort((a, b) =>
            sortOrder === 'asc'
                ? new Date(a.transaction.createdAt).getTime() - new Date(b.transaction.createdAt).getTime()
                : new Date(b.transaction.createdAt).getTime() - new Date(a.transaction.createdAt).getTime()
        );
    };

    const filterByTab = (list: InterbankTransaction[], tab: 'all' | 'in' | 'out') => {
        let filtered = list;
        if (tab === 'in') {
            filtered = list.filter(
                (transaction) =>
                    transaction.tab === 'in'
            );
        } else if (tab === 'out') {
            filtered = list.filter(
                (transaction) =>
                    transaction.tab === 'out'
            );
        } else {
            filtered = list;
        }
        dispatch(setFilteredTransactions(filtered));
    };

    // Hàm thay đổi tab
    const handleTabChange = (tab: 'all' | 'in' | 'out') => {
        dispatch(setActiveTab(tab));
        filterByTab(allTransactions, tab);
    };

    // Toggle sort order
    const toggleSortOrder = () => {
        const newOrder = sortOrder === 'asc' ? 'desc' : 'asc';
        dispatch(setSortOrder(newOrder));

        // Sort the transactions based on the new sort order
        const sortedTransactions = sortTransactions(filteredTransactions);
        filterByTab(sortedTransactions, activeTab);
    };

    return (
        <div className="min-h-screen w-[calc(100vw-300px)] max-w-full bg-gray-50 pt-7">
            <div className="w-full mx-auto bg-white shadow-lg rounded-lg">
                <div className="p-6 flex flex-col sm:flex-row items-center gap-4">
                    <input
                        type="date"
                        placeholder="Start date"
                        className="border border-gray-300 rounded-lg p-2 text-black"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />
                    <label className="text-gray-600"> - </label>
                    <input
                        type="date"
                        className="border border-gray-300 rounded-lg p-2 text-black"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                    />
                    {/* Sort button */}
                    <button
                        onClick={toggleSortOrder}
                        className="bg-gray-100 text-black px-4 py-2 rounded-lg hover:bg-gray-200"
                    >
                        Sort time: {sortOrder === 'asc' ? 'Newest' : 'Oldest'}
                    </button>
                    <label className="text-gray-600"> Bank Name: </label>
                        <select
                            id="bankSelect"
                            value={selectedBank}
                            onChange={handleBankChange}
                            className="border border-gray-300 rounded-lg p-2 text-black"
                        >
                            <option value="">Tất cả</option>
                            {banks.map((bank) => (
                                <option key={bank.id} value={bank.bankCode}>
                                    {bank.name}
                                </option>
                            ))}
                            <option value="-1">Unknown Bank</option>
                        </select>
                    <button
                        onClick={handleSearch}
                        className="bg-blue-500 text-white font-semibold py-2 px-6 rounded-lg hover:bg-blue-600"
                    >
                        Tìm kiếm
                    </button>
                </div>

                {/* Tabs */}
                <div className="flex border-b p-6 flex flex-col sm:flex-row items-center gap-4">
                    {['all', 'in', 'out'].map((tab) => (
                        <button
                            key={tab}
                            onClick={() => handleTabChange(tab as 'all' | 'in' | 'out')}
                            className={`w-1/3 p-4 text-center font-semibold text-black ${
                                activeTab === tab ? 'border-b-4 border-blue-500 text-blue-600' : 'text-gray-500'
                            }`}
                        >
                            {tab === 'all' ? 'Tất cả' : tab === 'in' ? 'Nhận tiền' : 'Chuyển tiền'}
                        </button>
                    ))}
                </div>

                {/* Error message */}
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}

                {/* Transaction List */}
                <div className="flex flex-col h-[calc(100vh-300px)] overflow-y-auto p-6">
                    {filteredTransactions.length > 0 ? (
                        filteredTransactions.map((transaction) => (
                            <div
                                key={`${transaction.id}-${transaction.date}`}
                                className={`flex justify-between items-center p-4 mb-4 rounded-lg shadow-sm ${
                                    transaction.bankName 
                                        ? transaction.tab === 'in' 
                                            ? 'bg-green-100 border-green-500' 
                                            : 'bg-orange-100 border-orange-500' 
                                        : transaction.tab === 'in' 
                                            ? 'bg-blue-100 border-blue-500' 
                                            : 'bg-red-100 border-red-500'
                                } border-l-4`}
                            >
                                <div>
                                    <p className="text-sm text-gray-600">
                                                <span
                                                    className="inline-block bg-blue-500 text-white px-2 py-1 rounded text-xs mb-2">
                                                    Liên ngân hàng
                                                </span>
                                        <span className="mx-2 py-1 rounded mb-2">
                                                    Ngân hàng: {transaction.bankName}
                                                </span>
                                    </p>
                                    {transaction.tab === 'in' ? (
                                        <p className="text-sm text-gray-600 my-2">
                                            Số tài khoản nguồn: {transaction.externalAccount}
                                        </p>
                                    ) : (
                                        <p className="text-sm text-gray-600 my-2">
                                            Số tài khoản đến: {transaction.externalAccount}
                                        </p>
                                    )}
                                    <p className="text-sm text-gray-600">
                                        {new Date(transaction.transaction.createdAt).toLocaleDateString()}
                                    </p>
                                </div>
                                <div
                                    className={`font-bold text-lg ${
                                        transaction.bankName
                                            ? transaction.tab === 'in'
                                                ? 'text-green-500'
                                                : 'text-orange-500'
                                            : transaction.tab === 'in'
                                                ? 'text-blue-500'
                                                : 'text-red-500'
                                    }`}
                                >
                                    {transaction.tab === 'in' ? '+' : '-'}
                                    {transaction.transaction.amount.toLocaleString()}₫
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="flex flex-col items-center mt-10">
                            <img src={NoDataImage} alt="No data" className="w-1/3" />
                            <p className="text-gray-500 mt-4">
                                Không có giao dịch {activeTab === 'in' ? 'nhận tiền' : activeTab === 'out' ? 'chuyển tiền' : 'trong thời gian đã chọn'}.
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TransactionHistoryAdmin;