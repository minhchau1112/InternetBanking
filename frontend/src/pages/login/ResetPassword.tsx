import React, { useState, useEffect } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { useSelector } from "react-redux";
import { RootState } from "@/redux/store";
import { useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import {resetPasswordAPI} from "@/api/emailAPI.ts";

const ResetPassword: React.FC = () => {
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const isOTPVerified = useSelector((state: RootState) => state.otp.isOTPVerified);
    const email = useSelector((state: RootState) => state.otp.email);
    const navigate = useNavigate();

    useEffect(() => {
        if (!isOTPVerified) {
            toast.error("Bạn cần xác nhận OTP trước khi đặt lại mật khẩu.");
            navigate("/forgot-password");
        }
    }, [isOTPVerified, navigate]);

    const handleResetPassword = async () => {
        if (!password || !confirmPassword) {
            toast.error("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (password !== confirmPassword) {
            toast.error("Mật khẩu không khớp. Vui lòng kiểm tra lại.");
            return;
        }

        try {
            const result = await resetPasswordAPI(email, password);
            console.log(result)
            if (result.error===null) {
                toast.success("Mật khẩu đã được đặt lại thành công.");
                setTimeout(() => {
                    navigate("/login");
                }, 3000);
            } else {
                toast.error(result.message);
            }
        } catch (error) {
            console.error("Error resetting password:", error);
            toast.error("Đã xảy ra lỗi khi đặt lại mật khẩu.");
        }
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <Card className="w-full max-w-md shadow-md">
                <CardHeader>
                    <h2 className="text-xl font-bold">Đặt lại mật khẩu</h2>
                    <p className="text-gray-500">Vui lòng nhập mật khẩu mới của bạn.</p>
                </CardHeader>
                <CardContent className="space-y-6">
                    <div>
                        <Label htmlFor="password">Mật khẩu mới</Label>
                        <Input
                            id="password"
                            type="password"
                            placeholder="Nhập mật khẩu mới"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <Label htmlFor="confirmPassword">Xác nhận mật khẩu</Label>
                        <Input
                            id="confirmPassword"
                            type="password"
                            placeholder="Nhập lại mật khẩu mới"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>
                    <Button className="w-full" onClick={handleResetPassword}>
                        Đặt lại mật khẩu
                    </Button>
                </CardContent>
            </Card>
            <ToastContainer />
        </div>
    );
};

export default ResetPassword;
