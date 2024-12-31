import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import React, { useState } from "react";
import ChangePassword from "@/pages/customer/ChangePassword.tsx";
import { Input } from "@/components/ui/input.tsx";

const Profile: React.FC = () => {
    const [name] = useState("John Doe");
    const [email] = useState("john.doe@example.com");
    const [phone] = useState("0335718277");

    return (
        <div className=" mx-20 py-8">
            <h1 className="text-2xl font-bold mb-4">Thông tin tài khoản</h1>
            {/* Display Customer Information */}
            <div className="mb-4">
                <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                    Họ và tên
                </label>
                <Input
                    id="name"
                    name="name"
                    type="text"
                    value={name}
                    disabled
                    className="mt-2 block w-full"
                />
            </div>

            <div className="mb-4">
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                    Email
                </label>
                <Input
                    id="email"
                    name="email"
                    type="email"
                    disabled
                    value={email}
                    className="mt-2 block w-full"
                />
            </div>
            <div className="mb-4">
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                    Số điện thoại
                </label>
                <Input
                    id="phone"
                    name="phone"
                    type="text"
                    disabled
                    value={phone}
                    className="mt-2 block w-full"
                />
            </div>

            {/* Change Password Button that triggers the dialog */}
            <Dialog>
                <DialogTrigger asChild>
                    <Button className="mt-6 hover:border-none hover:bg-black hover:animate-none">Đổi mật khẩu</Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-[480px]">
                    <DialogHeader>
                        <DialogTitle>Đổi mật khẩu</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <ChangePassword />
                    </div>
                    <DialogFooter>
                        {/* Add footer actions if needed */}
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
};

export default Profile;
