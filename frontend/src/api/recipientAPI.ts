import axios from "axios";

const API_URL = "http://localhost:8888/api";

export const fetchCustomerRecipients = async () => {
    try {
        const user = localStorage.getItem('user');
        const accessToken = localStorage.getItem('access_token');
        if (!user) {
            throw new Error('User information not found in localStorage');
        }

        const { userID } = JSON.parse(user);
        if (!userID) {
            throw new Error('userID is missing in localStorage');
        }

        const response = await axios.get(`${API_URL}/recipients/${userID}`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        });

        console.log(response.data.data);
        return response.data.data;
    } catch (error) {
        console.error(error);
    }
};

export const deleteRecipients = async (recipientId: number) => {
    try {

        const accessToken = localStorage.getItem('access_token');
        const response = await axios.delete(`${API_URL}/recipients/${recipientId}`,
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            }
        );

        console.log(response.data.message);
        return response.data.message;
    } catch (error) {
        console.error(error);
    }
};

export const createRecipient = async (accountNumber:string, aliasName:string, bankCode:string) => {
    try {
        const user = localStorage.getItem('user');
        const accessToken = localStorage.getItem('access_token');
        if (!user) {
            throw new Error('User information not found in localStorage');
        }

        const { userID } = JSON.parse(user);
        if (!userID) {
            throw new Error('userID is missing in localStorage');
        }

        let saveAliasName = aliasName;
        if(bankCode == "WNC"){
            const WNCaccount = await getWNCAcccount(accountNumber);
            if (saveAliasName === "") {
                saveAliasName = (WNCaccount.firstName || "")
                    + (WNCaccount.lastName ? " " + WNCaccount.lastName : "");
            }
        }

        const response = await axios.post(`${API_URL}/recipients`,
            {
                customerId: userID,
                accountNumber: accountNumber,
                aliasName: saveAliasName,
                bankCode: bankCode,
            },
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            }
        );

        console.log(response.data.message);
        return response.data.message;
    } catch (error) {
        console.error(error);
    }
};

export const updateRecipient = async (updatingRecipientId:number, accountNumber:string, aliasName:string, bankCode:string) => {
    try {
        const accessToken = localStorage.getItem('access_token');

        const response = await axios.put(`${API_URL}/recipients/${updatingRecipientId}`,
            {
                recipientId: updatingRecipientId,
                accountNumber: accountNumber,
                aliasName: aliasName,
                bankCode: bankCode,
            },
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            }
        );

        console.log(response.data.message);
        return response.data.message;
    } catch (error) {
        console.error(error);
    }
};

export const getWNCAcccount = async (accountNumber:string) => {

    try {
        const accessToken = localStorage.getItem('access_token');

        const response = await axios.post(`${API_URL}/interbank/get-account-info/`,
            {
                account_number: accountNumber
            },
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                },
            });

        console.log(response.data.data);
        return response.data.data;
    } catch (error) {
        console.log("WNC account not found.");
    }
};