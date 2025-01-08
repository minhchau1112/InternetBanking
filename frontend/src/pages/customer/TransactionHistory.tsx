import { useState } from 'react';
import NoDataImage from '@/assets/image/nodata.png';
import axios from "axios";

type BaseTransaction = {
    id: number;
    amount: number;
    date: string;
    description: string;
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
                // Tạo chuỗi query parameters cho các tham số tìm kiếm
                params: new URLSearchParams({
                    accountId: sourceAccountId || '',
                    partnerAccountNumber: partnerAccountNumber || '',
                    startDate: startDate || '',
                    endDate: endDate || '',
                    type: transactionType || 'internal',
                    tab: activeTab,
                }),
            });

            const data = await response.data.data;

            console.log('Data:', data);

            if (data.length === 0) {
                setError('Không có dữ liệu giao dịch.');
                return;
            }

            // Sử dụng map để chuyển đổi dữ liệu thành đối tượng Transaction
            const transactions: CombinedTransaction[] = data.map((item: any) => {
                const transaction = item.transaction;
                if (item.bankName) {
                    return {
                        id: transaction.id,
                        amount: transaction.amount,
                        date: transaction.createdAt,
                        description: transaction.message,
                        bankName: item.bankName,
                        tab: item.tab,
                    } as InterbankTransaction;
                } else {
                    return {
                        id: transaction.id,
                        type: transaction.type,
                        amount: transaction.amount,
                        date: transaction.createdAt,
                        description: transaction.message,
                        tab: item.tab,
                    } as InternalTransaction;
                }
            });

            setAllTransactions(transactions); // Lưu tất cả giao dịch
            filterByTab(transactions, activeTab); // Lọc giao dịch theo tab
        } catch (err) {
            setError('Không thể tải dữ liệu. Vui lòng thử lại sau.');
            console.error('Failed to fetch transactions:', err);
        }
    };

    // Hàm tìm kiếm
    const handleSearch = () => {
        fetchTransactions();
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
    };

    return (
        <div className="min-h-screen w-[calc(100vw-300px)] max-w-full bg-gray-50 p-6">
            <div className="w-full mx-auto bg-white shadow-lg rounded-lg">
                <div className="p-6 flex flex-col sm:flex-row items-center gap-4">
                    <input
                        type="text"
                        placeholder="Số tài khoản"
<<<<<<<< HEAD:frontend/src/pages/employee/TransactionHistory.tsx
                        className="border border-gray-300 rounded-lg p-2 flex-1 text-black"
                        value={destinationAccountNumber}
                        onChange={(e) => setDestinationAccountNumber(e.target.value)}
========
                        className="border border-gray-300 rounded-lg p-2 flex-1 text-white"
                        value={partnerAccountNumber}
                        onChange={(e) => setPartnerAccountNumber(e.target.value)}
>>>>>>>> origin/main:frontend/src/pages/customer/TransactionHistory.tsx
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
                    <select
                        className="border border-gray-300 rounded-lg p-2 text-black"
                        value={transactionType}
                        onChange={(e) => setTransactionType(e.target.value as 'internal' | 'interbank')}
                    >
                        <option value="internal">Nội bộ</option>
                        <option value="interbank">Liên ngân hàng</option>
                    </select>
                    <button
                        onClick={handleSearch}
                        className="bg-blue-500 text-white font-semibold py-2 px-6 rounded-lg hover:bg-blue-600"
                    >
                        Tìm kiếm
                    </button>
                </div>

                {/* Tabs */}
                <div className="flex border-b p-6 flex-col sm:flex-row items-center gap-4">
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
                        // Dùng map để render danh sách giao dịch
                        filteredTransactions.map((transaction) => (
                            <div
                                key={transaction.id}
                                className="flex justify-between items-center p-4 mb-4 rounded-lg shadow-sm bg-gray-100"
                            >
                                <div>
                                    <p className="font-semibold">{transaction.description}</p>
<<<<<<<< HEAD:frontend/src/pages/employee/TransactionHistory.tsx
                                    {transaction.type.startsWith('interbank') && 'bankName' in transaction && (
                                        <p className="text-sm text-gray-600">Ngân hàng: {transaction.bankName}</p>
========
                                    {transactionType === 'interbank' && (
                                        <p className="text-sm text-gray-600">
                                            Ngân hàng: {transaction.bankName || 'Không xác định'}
                                        </p>
>>>>>>>> origin/main:frontend/src/pages/customer/TransactionHistory.tsx
                                    )}
                                    {transactionType === 'internal' && (
                                        <p className="text-sm text-gray-600">
                                            Loại giao dịch: {transaction.type.toLowerCase() === 'transfer' ? 'Giao dịch' : 'Trả nợ'}
                                        </p>
                                    )}
                                    <p className="text-sm text-gray-600">
                                        {new Date(transaction.date).toLocaleDateString()}
                                    </p>
                                </div>
                                <div
                                    className={`font-bold text-lg ${
                                        transaction.tab === 'in'
                                            ? 'text-green-500'
                                            : 'text-red-500'
                                    }`}
                                >
<<<<<<<< HEAD:frontend/src/pages/employee/TransactionHistory.tsx
                                    {transaction.type === 'received' || transaction.type === 'interbank_received' ? '+' : '-'}                                    {transaction.amount.toLocaleString()}₫
========
                                    {transaction.tab === 'in' ? '+' : '-'}
                                    {transaction.amount.toLocaleString()}₫
>>>>>>>> origin/main:frontend/src/pages/customer/TransactionHistory.tsx
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
