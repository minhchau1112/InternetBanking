import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog';
import ChangePassword from '@/pages/customer/ChangePassword.tsx';
import { Input } from '@/components/ui/input.tsx';
import { ToastContainer, toast } from 'react-toastify';
import { fetchCustomerDetails, fetchCustomerAccounts } from '@/api/authAPI.ts'; // Import the API function

type Account = {
    accountNumber: string;
    accountType: string;
    balance: string;
    createdAt: string;
    ownerName: string;
}
const Profile: React.FC = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [accounts, setAccounts] = useState<Account[]>([]);

    useEffect(() => {
        const loadCustomerData = async () => {
            try {
                const customerData = await fetchCustomerDetails();
                setName(customerData.name);
                setEmail(customerData.email);
                setPhone(customerData.phone);
            } catch (error) {
                console.log(error);
                toast.error('Không thể tải thông tin tài khoản. Vui lòng thử lại.');
            }
        };

        loadCustomerData();
    }, []);

    const handlePasswordChangeSuccess = () => {
        setIsDialogOpen(false);
    };
    useEffect(() => {
        const loadCustomerAccount = async () => {
            try{
                const loadAccount = await fetchCustomerAccounts();
                setAccounts(loadAccount);
            }catch (e) {
                console.log(e);
                toast.error("Error fetch customer accounts");
            }
        }

        loadCustomerAccount();
    }, []);

    return (
        <div className="mx-20 py-8 ">
            <div className="w-full h-full grid grid-cols-3 gap-8">
                <div className="w-full col-span-2">
                    <h1 className="text-2xl font-bold mb-4">Thông tin tài khoản</h1>

                    <div className="mb-4">
                        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                            Họ và tên
                        </label>
                        <Input id="name" name="name" type="text" value={name} disabled className="mt-2 block w-full"/>
                    </div>

                    <div className="mb-4">
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                            Email
                        </label>
                        <Input id="email" name="email" type="email" value={email} disabled
                               className="mt-2 block w-full"/>
                    </div>

                    <div className="mb-4">
                        <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
                            Số điện thoại
                        </label>
                        <Input id="phone" name="phone" type="text" value={phone} disabled
                               className="mt-2 block w-full"/>
                    </div>

                    <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                        <DialogTrigger asChild>
                            <Button className="mt-6 hover:border-none hover:bg-black hover:animate-none">
                                Đổi mật khẩu
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[480px]">
                            <DialogHeader>
                                <DialogTitle>Đổi mật khẩu</DialogTitle>
                            </DialogHeader>
                            <ChangePassword onSuccess={handlePasswordChangeSuccess}/>
                        </DialogContent>
                    </Dialog>
                </div>
                <div className="w-full flex flex-col space-y-2 mb-5">
                    <h1 className="text-3xl font-bold mb-3 text-gray-800 text-left">My Accounts</h1>
                    <div className="w-full flex space-x-4 overflow-auto">
                        {accounts.map((account) => (
                            <div key={account.accountNumber} className="w-80 h-48 rounded-2xl bg-cover bg-center p-5
                                bg-[url('src/assets/image/card-bg.png')]"
                            >
                                <img src="src/assets/sim-vector.svg" alt="" className="mb-5"/>
                                <p className="text-sm font-light text-white mb-1">Total Balance</p>
                                <p className="text-xl font-bold text-white mb-2">{account.balance}VNĐ</p>
                                <p className="text-md font-light text-white mb-1">{account.accountNumber}</p>
                                <p className="text-sm font-light text-white">{account.createdAt}</p>
                            </div>
                        ))}

                    </div>
                </div>
            </div>


            <ToastContainer/>
        </div>
    );
};

export default Profile;
