import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { RootState } from "@/redux/store";
import { toast } from "react-toastify";

const ResetPassword: React.FC = () => {
    const isOTPVerified = useSelector((state: RootState) => state.otp.isOTPVerified);
    const navigate = useNavigate();

    useEffect(() => {
        if (!isOTPVerified) {
            toast.error("Bạn cần xác nhận OTP trước khi đặt lại mật khẩu.");
            navigate("/forgot-password");
        }
    }, [isOTPVerified, navigate]);

    return (
        <div>
            <h1>Trang Đặt Lại Mật Khẩu</h1>
        </div>
    );
};

export default ResetPassword;
