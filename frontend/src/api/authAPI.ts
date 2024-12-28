import axios from "axios";

const API_BASE_URL = "http://localhost:8888/api/auth";

export const login = async (username: string, password: string) => {
    const response = await axios.post(
        `${API_BASE_URL}/login`,
        { username, password },
        { withCredentials: true }
    );
    return response.data.data;
};