import { useState } from 'react';
import NoDataImage from '@/assets/image/nodata.png';
import axios from "axios";

type BaseTransaction = {
    id: number;
    amount: number;
    date: string;
    description: string;
    accountNumber: string;
    tab: 'in' | 'out';
};

type InternalTransaction = BaseTransaction & {
    type: 'transfer' | 'debt_payment';
};

type InterbankTransaction = BaseTransaction & {
    bankName: string;
};

type CombinedTransaction = InternalTransaction | InterbankTransaction;

const TransactionHistory = () => {
    const [activeTab, setActiveTab] = useState<'all' | 'in' | 'out'>('all');
    const [sourceAccountId] = useState<string>(localStorage.getItem('accountId') || '');
    const [partnerAccountNumber, setPartnerAccountNumber] = useState<string>('');
    const [transactionType, setTransactionType] = useState<'internal' | 'interbank'>('internal');
    const [startDate, setStartDate] = useState<string>('');
    const [endDate, setEndDate] = useState<string>('');
    const [allTransactions, setAllTransactions] = useState<CombinedTransaction[]>([]);
    const [filteredTransactions, setFilteredTransactions] = useState<CombinedTransaction[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');

    // Hàm fetch dữ liệu giao dịch từ API
    const fetchTransactions = async () => {
        try {
            setError(null); // Clear previous errors
            const token = localStorage.getItem('access_token');
            const response = await axios.get('http://localhost:8888/api/transactions', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                params: new URLSearchParams({
                    accountId: sourceAccountId || '',
                    partnerAccountNumber: partnerAccountNumber || '',
                    startDate: startDate || '',
                    endDate: endDate || '',
                    type: transactionType || 'internal', // This will fetch both if not filtered
                    tab: activeTab,
                }),
            });
    
            const data = await response.data.data;
    
            if (data.length === 0) {
                setError('Không có dữ liệu giao dịch.');
                return;
            }
    
            console.log(data);
            // Map response data into transactions array
            const transactions: CombinedTransaction[] = data.map((item: any) => {
                const transaction = item.transaction;
                if (item.bankName) { // Interbank transaction
                    return {
                        id: transaction.id,
                        amount: transaction.amount,
                        date: transaction.createdAt,
                        description: transaction.message,
                        bankName: item.bankName,
                        tab: item.tab,
                        accountNumber: item.externalAccount
                    } as InterbankTransaction;
                } else { // Internal transaction
                    return {
                        id: transaction.id,
                        type: transaction.type,
                        amount: transaction.amount,
                        date: transaction.createdAt,
                        description: transaction.message,
                        accountNumber: transaction.sourceAccount.id === parseInt(sourceAccountId) ? transaction.destinationAccount.accountNumber : transaction.sourceAccount.accountNumber,
                        tab: item.tab,
                    } as InternalTransaction;
                }
            });

            // print out the transactions through mapping
            // transactions.forEach(transaction => console.log(transaction));
    
            setAllTransactions(transactions); // Store all transactions
            filterByTab(transactions, activeTab); // Filter by tab (all, in, out)
        } catch (err) {
            setError('Không thể tải dữ liệu. Vui lòng thử lại sau.');
            console.error('Failed to fetch transactions:', err);
        }
    };    

    // Hàm tìm kiếm
    const handleSearch = () => {
        fetchTransactions();
    };

    // Sort transactions
    const sortTransactions = (list: CombinedTransaction[]) => {
        return [...list].sort((a, b) =>
            sortOrder === 'asc'
                ? new Date(a.date).getTime() - new Date(b.date).getTime()
                : new Date(b.date).getTime() - new Date(a.date).getTime()
        );
    };

    const filterByTab = (list: CombinedTransaction[], tab: 'all' | 'in' | 'out') => {
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
        }
        setFilteredTransactions(filtered);
    };

    // Hàm thay đổi tab
    const handleTabChange = (tab: 'all' | 'in' | 'out') => {
        setActiveTab(tab);
        filterByTab(allTransactions, tab);
    };

    // Toggle sort order
    const toggleSortOrder = () => {
        const newOrder = sortOrder === 'asc' ? 'desc' : 'asc';
        setSortOrder(newOrder);
        setFilteredTransactions(sortTransactions(filteredTransactions));
    };

    return (
        <div className="min-h-screen w-[calc(100vw-300px)] max-w-full bg-gray-50 pt-7">
            <div className="w-full mx-auto bg-white shadow-lg rounded-lg">
                <div className="p-6 flex flex-col sm:flex-row items-center gap-4">
                    <input
                        type="text"
                        placeholder="Số tài khoản để lọc"
                        className="border border-gray-300 rounded-lg p-2 flex-1 text-black"
                        value={partnerAccountNumber}
                        onChange={(e) => setPartnerAccountNumber(e.target.value)}
                    />
                    <input
                        type="date"
                        className="border border-gray-300 rounded-lg p-2 text-black"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />
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
                                    {/* <p className="font-semibold">{transaction.description}</p> */}
                                    
                                    {/* Add badge and extra details based on bankName */}
                                    {transaction.bankName ? (
                                        <>
                                            <p className="text-sm text-gray-600">
                                                <span className="inline-block bg-blue-500 text-white px-2 py-1 rounded text-xs mb-2">
                                                    Liên ngân hàng
                                                </span>
                                                <span className="mx-2 py-1 rounded mb-2">
                                                    Ngân hàng: {transaction.bankName}
                                                </span>
                                            </p>
                                            <p className="text-sm text-gray-600 my-2">
                                                Số tài khoản nhận: {transaction.accountNumber}
                                            </p>
                                        </>
                                    ) : (
                                        <>
                                            <p className="text-sm text-gray-600 my-2">
                                                <span className="inline-block bg-green-500 text-white px-2 py-1 rounded text-xs mr-2">
                                                    Nội bộ
                                                </span>
                                                <span className="text-gray px-2 py-1 rounded mr-2">
                                                    Loại giao dịch: {transaction.type}
                                                </span>
                                            </p>
                                            <p className="text-sm text-gray-600">
                                                Số tài khoản nhận: {transaction.accountNumber}
                                            </p>
                                        </>
                                    )}

                                    <p className="text-sm text-gray-600">
                                        {new Date(transaction.date).toLocaleDateString()}
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
                                    {transaction.amount.toLocaleString()}₫
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

export default TransactionHistory;