import React, { useState, useRef } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { FiEdit } from "react-icons/fi";

const ForgotPassword: React.FC = () => {
    const [email, setEmail] = useState("");
    const [OTP, setOTP] = useState<string[]>(Array(6).fill(""));
    const [isEmailSubmitted, setIsEmailSubmitted] = useState(false);
    const [isValidOTP, setIsValidOTP] = useState<boolean | null>(null);

    const otpRefs = useRef<(HTMLInputElement | null)[]>([]);

    const handleEmailSubmit = () => {
        console.log("Email được gửi tới API:", email);
        setIsEmailSubmitted(true);
    };

    const handleOTPSubmit = (enteredOtp: string) => {
        console.log("OTP được gửi tới API:", enteredOtp);
        if (enteredOtp === "123456") {
            setIsValidOTP(true);
            alert("OTP xác nhận thành công! Một liên kết để đặt lại mật khẩu đã được gửi.");
        } else {
            setIsValidOTP(false);
        }
    };

    const handleEditEmail = () => {
        setIsEmailSubmitted(false);
        setOTP(Array(6).fill(""));
        setIsValidOTP(null);
    };

    const handleOTPChange = (value: string, index: number) => {
        if (isNaN(Number(value))) return;
        const newOTP = [...OTP];
        newOTP[index] = value;
        setOTP(newOTP);

        if (value && index < 5) {
            otpRefs.current[index + 1]?.focus();
        }

        const enteredOtp = newOTP.join("");
        if (enteredOtp.length === 6 && !newOTP.includes("")) {
            handleOTPSubmit(enteredOtp);
        }
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (event.key === "Backspace" && OTP[index] === "" && index > 0) {
            otpRefs.current[index - 1]?.focus();
        }
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <Card className="w-full max-w-md shadow-md">
                <CardHeader>
                    <h2 className="text-xl font-bold">Quên mật khẩu</h2>
                    <div className="text-gray-500">
                        {isEmailSubmitted ? (
                            <div>
                                <span>Mã OTP đã được gửi tới email: </span>
                                <div className="flex mt-2 items-center">
                                    <b>{email}</b>
                                    <FiEdit
                                        onClick={handleEditEmail}
                                        className="ml-2 cursor-pointer text-gray-500 hover:text-gray-700"
                                        title="Chỉnh sửa email"
                                    />
                                </div>
                            </div>
                        ) : (
                            <p>Nhập email để lấy mã OTP.</p>
                        )}
                    </div>
                </CardHeader>
                <CardContent className="space-y-6">
                    {isEmailSubmitted ? (
                        <>
                            <Label htmlFor="OTP">Nhập OTP</Label>
                            <div className="flex justify-center items-center space-x-2">
                                {OTP.map((digit, index) => (
                                    <Input
                                        key={index}
                                        ref={(el) => (otpRefs.current[index] = el)}
                                        id={`OTP-input-${index}`}
                                        type="text"
                                        maxLength={1}
                                        value={digit}
                                        onChange={(e) => handleOTPChange(e.target.value, index)}
                                        onKeyDown={(e) => handleKeyDown(e, index)}
                                        className="w-12 h-12 text-center text-lg border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                ))}
                            </div>
                            {isValidOTP === false && (
                                <p className="text-sm text-red-500 mt-2">OTP không hợp lệ. Vui lòng thử lại.</p>
                            )}
                        </>
                    ) : (
                        <>
                            <Label htmlFor="email">Nhập email</Label>
                            <Input
                                id="email"
                                type="email"
                                placeholder="Nhập email của bạn"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                            <Button onClick={handleEmailSubmit} className="w-full">
                                Gửi mã OTP
                            </Button>
                        </>
                    )}
                </CardContent>
            </Card>
        </div>
    );
};

export default ForgotPassword;
