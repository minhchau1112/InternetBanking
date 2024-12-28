import React, { useState, useRef, useEffect } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { FiEdit, FiArrowLeft } from "react-icons/fi";
import { toast, ToastContainer } from "react-toastify";
import { sendForgotPasswordEmail, verifyOTP } from "@/api/emailAPI.ts";

const ForgotPassword: React.FC = () => {
    const [email, setEmail] = useState("");
    const [OTP, setOTP] = useState<string[]>(Array(6).fill(""));
    const [isEmailSubmitted, setIsEmailSubmitted] = useState(false);
    const [isValidOTP, setIsValidOTP] = useState<boolean | null>(null);
    const [timer, setTimer] = useState(60);
    const otpRefs = useRef<(HTMLInputElement | null)[]>([]);
    const intervalRef = useRef<NodeJS.Timeout | null>(null);

    useEffect(() => {
        if (isEmailSubmitted) {
            setTimer(60);
            intervalRef.current = setInterval(() => {
                setTimer((prev) => {
                    if (prev === 1 && intervalRef.current) {
                        clearInterval(intervalRef.current);
                    }
                    return prev - 1;
                });
            }, 1000);
        }
        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, [isEmailSubmitted]);

    const handleEmailSubmit = async () => {
        const result = await sendForgotPasswordEmail(email);
        if (result.success) {
            setIsEmailSubmitted(true);
            setTimer(60);
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
            intervalRef.current = setInterval(() => {
                setTimer((prev) => {
                    if (prev === 1 && intervalRef.current) {
                        clearInterval(intervalRef.current);
                    }
                    return prev - 1;
                });
            }, 1000);
            toast.success("Mã OTP đã được gửi đến email của bạn.");
        } else {
            toast.error(result.message);
        }
    };

    const handleEditEmail = () => {
        setIsEmailSubmitted(false);
        setOTP(Array(6).fill(""));
        setIsValidOTP(null);
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
        }
    };

    const handleOTPSubmit = async (enteredOtp: string) => {
        try {
            const response = await verifyOTP(email, enteredOtp);
            if (response.status === 200) {
                setIsValidOTP(true);
                if (intervalRef.current) {
                    clearInterval(intervalRef.current);
                    intervalRef.current = null;
                }
                toast.success("OTP xác nhận thành công!");
            } else {
                setIsValidOTP(false);
                toast.error("OTP không hợp lệ.");
            }
        } catch (error) {
            console.error("Error verifying OTP:", error);
            toast.error("Lỗi kết nối với server.");
        }
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
                            <div className="flex justify-center items-center space-x-2 pb-5">
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
                            <div className="text-center mt-4">
                                {timer > 0 ? (
                                    <div className="inline-flex items-center space-x-2">
                                        <span className="text-gray-500 text-sm">Mã OTP sẽ hết hạn sau {timer}s</span>
                                    </div>
                                ) : (
                                    <div className="flex flex-col items-center space-y-4">
                                        <p className="text-red-500 text-sm font-semibold">Mã OTP đã hết hạn.</p>
                                        <Button onClick={handleEmailSubmit} className="w-full hover:border-none">
                                            Gửi lại mã OTP
                                        </Button>
                                    </div>
                                )}
                            </div>
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
                            <div
                                className="text-sm flex items-center text-gray-600 bg-transparent border-none p-0 m-0 focus:outline-none hover:no-underline">
                                <FiArrowLeft className="mr-1 text-gray-600" />
                                <a
                                    href="/login"
                                    className="text-gray-600 hover:no-underline hover:text-gray-800"
                                >
                                    Quay về trang đăng nhập
                                </a>
                            </div>
                        </>
                    )}
                </CardContent>
            </Card>
            <ToastContainer />
        </div>
    );
};

export default ForgotPassword;
