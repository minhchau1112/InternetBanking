import axios, {AxiosError} from "axios";
import {toast} from "react-toastify";

const API_BASE_URL = "http://localhost:8888/api/auth";

export const verifyEmail = async (email: string) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/verify-email`, { email });
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            if (error.response?.status === 404) {
                toast.error("Email không hợp lệ hoặc không tồn tại.");
            } else {
                toast.error(error.response?.data?.message || "Có lỗi xảy ra khi gửi yêu cầu.");
            }
        } else {
            toast.error("Có lỗi xảy ra, vui lòng thử lại.");
        }
        throw error;
    }
};
export const sendForgotPasswordEmail = async (email: string) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/forgot-password`, {
            email: email,
        });

        if (response.status === 200 && response.data && response.data.status === 200) {
            return { success: true, message: "Đã gửi mã OTP thành công." };
        } else {
            return { success: false, message: response.data.message || "Có lỗi xảy ra." };
        }
    } catch (error) {
        console.error("Error during email verification:", error);
        return { success: false, message: "Lỗi kết nối với server." };
    }
};
export const verifyOTP = async (email: string, otp: string) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/verify-reset-otp`, { email, otp });
        return response.data;
    } catch (error) {
        if (error instanceof AxiosError && error.response) {
            return error.response.data;
        }
        throw new Error("Lỗi kết nối với server");
    }
};