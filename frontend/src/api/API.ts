import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8888/api", // Thay URL này bằng địa chỉ backend của bạn
});

API.interceptors.request.use((req) => {
    const token = localStorage.getItem("token"); // Lấy token từ localStorage
    if (token) {
        req.headers.Authorization = `Bearer ${token}`; // Gắn token vào header
    }
    return req;
});

export default API;
