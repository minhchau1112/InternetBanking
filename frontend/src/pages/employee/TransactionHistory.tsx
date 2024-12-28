import React, { useState } from 'react';

type Transaction = {
    id: number;
    type: 'received' | 'transfer' | 'debt_payment';
    amount: number;
    date: string;
    description: string;
};

const TransactionHistory = () => {
    const [activeTab, setActiveTab] = useState<'all' | 'in' | 'out'>('all');
    const [accountNumber, setAccountNumber] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [allTransactions, setAllTransactions] = useState<Transaction[]>([]);
    const [filteredTransactions, setFilteredTransactions] = useState<Transaction[]>([]);

    const transactions = [
        { id: 1, type: 'received', amount: 500000, date: '2024-12-28', description: 'Nhận tiền từ A' },
        { id: 2, type: 'transfer', amount: 300000, date: '2024-12-27', description: 'Chuyển khoản đến B' },
        { id: 3, type: 'debt_payment', amount: 100000, date: '2024-12-26', description: 'Thanh toán nhắc nợ' },
        { id: 4, type: 'received', amount: 700000, date: '2024-12-25', description: 'Nhận tiền từ C' },
        { id: 5, type: 'transfer', amount: 200000, date: '2024-12-24', description: 'Chuyển khoản đến D' },
        { id: 6, type: 'received', amount: 500000, date: '2024-12-28', description: 'Nhận tiền từ A' },
        { id: 7, type: 'transfer', amount: 300000, date: '2024-12-27', description: 'Chuyển khoản đến B' },
        { id: 8, type: 'debt_payment', amount: 100000, date: '2024-12-26', description: 'Thanh toán nhắc nợ' },
        { id: 9, type: 'received', amount: 700000, date: '2024-12-25', description: 'Nhận tiền từ C' },
        { id: 10, type: 'transfer', amount: 200000, date: '2024-12-24', description: 'Chuyển khoản đến D' },
    ];

    const getTypeStyle = (type: string) => {
        switch (type) {
            case 'received':
                return 'bg-green-100 text-green-700';
            case 'transfer':
                return 'bg-blue-100 text-blue-700';
            case 'debt_payment':
                return 'bg-red-100 text-red-700';
            default:
                return 'bg-gray-100 text-gray-700';
        }
    };

    const handleSearch = () => {
        // Filter by date range
        let filtered = transactions;
        if (startDate || endDate) {
            filtered = filtered.filter((transaction) => {
                const transactionDate = new Date(transaction.date).getTime();
                const start = startDate ? new Date(startDate).getTime() : -Infinity;
                const end = endDate ? new Date(endDate).getTime() : Infinity;
                return transactionDate >= start && transactionDate <= end;
            });
        }

        setAllTransactions(filtered as Transaction[]);
        filterByTab(filtered as Transaction[], activeTab); // Filter for the current tab
    };

    const filterByTab = (list: Transaction[], tab: 'all' | 'in' | 'out') => {
        let filtered = list;
        if (tab === 'in') {
            filtered = list.filter((transaction) => transaction.type === 'received');
        } else if (tab === 'out') {
            filtered = list.filter((transaction) => transaction.type !== 'received');
        }
        setFilteredTransactions(filtered);
    };

    const handleTabChange = (tab: 'all' | 'in' | 'out') => {
        setActiveTab(tab);
        filterByTab(allTransactions, tab);
    };

    return (
        <div className="min-h-screen w-[calc(100vw-256px)] max-w-full bg-gray-50 p-6">
            <div className="w-full mx-auto bg-white shadow-lg rounded-lg">
                {/* Input Row */}
                <div className="p-6 flex flex-col sm:flex-row items-center gap-4">
                    <input
                        type="text"
                        placeholder="Số tài khoản"
                        className="border border-gray-300 rounded-lg p-2 flex-1"
                        value={accountNumber}
                        onChange={(e) => setAccountNumber(e.target.value)}
                    />
                    <input
                        type="date"
                        className="border border-gray-300 rounded-lg p-2"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />
                    <input
                        type="date"
                        className="border border-gray-300 rounded-lg p-2"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                    />
                    <button
                        onClick={handleSearch}
                        className="bg-blue-500 text-white font-semibold py-2 px-6 rounded-lg hover:bg-blue-600"
                    >
                        Tìm kiếm
                    </button>
                </div>

                {/* Tabs */}
                <div className="flex border-b">
                    <button
                        onClick={() => handleTabChange('all')}
                        className={`w-1/3 p-4 text-center font-semibold ${
                            activeTab === 'all' ? 'border-b-4 border-blue-500 text-blue-600' : 'text-gray-500'
                        }`}
                    >
                        Tất cả
                    </button>
                    <button
                        onClick={() => handleTabChange('in')}
                        className={`w-1/3 p-4 text-center font-semibold ${
                            activeTab === 'in' ? 'border-b-4 border-blue-500 text-blue-600' : 'text-gray-500'
                        }`}
                    >
                        Nhận tiền
                    </button>
                    <button
                        onClick={() => handleTabChange('out')}
                        className={`w-1/3 p-4 text-center font-semibold ${
                            activeTab === 'out' ? 'border-b-4 border-blue-500 text-blue-600' : 'text-gray-500'
                        }`}
                    >
                        Chuyển tiền
                    </button>
                </div>

                {/* Transaction List scrollable */}
                <div className="flex flex-col h-[calc(100vh-200px)] overflow-y-auto p-6">
                    {filteredTransactions.length > 0 ? (
                        filteredTransactions.map((transaction) => (
                            <div
                                key={transaction.id}
                                className={`flex justify-between items-center p-4 mb-4 rounded-lg shadow-sm ${getTypeStyle(
                                    transaction.type
                                )}`}
                            >
                                <div>
                                    <p className="font-semibold">{transaction.description}</p>
                                    <p className="text-sm text-gray-600">{transaction.date}</p>
                                </div>
                                <div className="font-bold text-lg">
                                    {transaction.type === 'received' ? '+' : '-'}
                                    {transaction.amount.toLocaleString()}₫
                                </div>
                            </div>
                        ))
                    ) : (
                        <p className="text-gray-500 text-center">Không có giao dịch trong thời gian đã chọn.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TransactionHistory;
