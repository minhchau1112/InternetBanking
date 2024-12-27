import axios from "axios";
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