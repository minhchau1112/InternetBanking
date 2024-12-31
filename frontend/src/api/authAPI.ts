import axios from "axios";

const API_BASE_URL = "http://localhost:8888/api/auth";

export const login = async (username: string, password: string, recaptchaResponse: string) => {
    const response = await axios.post(
        `${API_BASE_URL}/login`,
        { username, password },
        {
            params: { recaptchaResponse },
            withCredentials: true
        }
    );
    return response.data.data;
};





