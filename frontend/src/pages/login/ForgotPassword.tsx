import React, { useEffect, useState } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const MOCK_EMAIL = "user@example.com";
const MOCK_OTP = "123456";

const ForgotPassword: React.FC = () => {
    const [otp, setOtp] = useState("");
    const [isValidOtp, setIsValidOtp] = useState<boolean | null>(null);

    useEffect(() => {
        console.log("OTP đã được gửi đến:", MOCK_EMAIL);
    }, []);

    useEffect(() => {
        if (otp.length === 6) {
            if (otp === MOCK_OTP) {
                setIsValidOtp(true);
                alert("OTP xác nhận thành công! Một liên kết để đặt lại mật khẩu đã được gửi.");
            } else {
                setIsValidOtp(false);
            }
        } else {
            setIsValidOtp(null);
        }
    }, [otp]);

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <Card className="w-full max-w-md shadow-md">
                <CardHeader>
                    <h2 className="text-xl font-bold">Quên mật khẩu</h2>
                    <p className="text-gray-500">
                        Chúng tôi đã gửi OTP tới email của bạn: <b>{MOCK_EMAIL}</b>.
                    </p>
                </CardHeader>
                <CardContent className="space-y-4">
                    <Label htmlFor="otp">Nhập OTP</Label>
                    <Input
                        id="otp"
                        type="text"
                        maxLength={6}
                        placeholder="Nhập OTP"
                        value={otp}
                        onChange={(e) => setOtp(e.target.value)}
                        required
                        className={isValidOtp === false ? "border-red-500" : ""}
                    />
                    {isValidOtp === false && (
                        <p className="text-sm text-red-500">OTP không hợp lệ. Vui lòng thử lại.</p>
                    )}
                </CardContent>
            </Card>
        </div>
    );
};

export default ForgotPassword;
