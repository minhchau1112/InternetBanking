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
import { fetchCustomerDetails } from '@/api/authAPI.ts'; // Import the API function

const Profile: React.FC = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [isDialogOpen, setIsDialogOpen] = useState(false);

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

    return (
        <div className="mx-20 py-8">
            <h1 className="text-2xl font-bold mb-4">Thông tin tài khoản</h1>

            <div className="mb-4">
                <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                    Họ và tên
                </label>
                <Input id="name" name="name" type="text" value={name} disabled className="mt-2 block w-full" />
            </div>

            <div className="mb-4">
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                    Email
                </label>
                <Input id="email" name="email" type="email" value={email} disabled className="mt-2 block w-full" />
            </div>

            <div className="mb-4">
                <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
                    Số điện thoại
                </label>
                <Input id="phone" name="phone" type="text" value={phone} disabled className="mt-2 block w-full" />
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
                    <ChangePassword onSuccess={handlePasswordChangeSuccess} />
                </DialogContent>
            </Dialog>
            <ToastContainer />
        </div>
    );
};

export default Profile;
