import React from 'react'
import axios from 'axios'
import { API_BASE_URL } from '../../../../config'

export const refreshKakaoAccessToken = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/bids/refresh-token`);
        console.log(response)
        return response?.data?.accessToken;
    } catch (error) {
        console.error("Error refreshing Kakao access token:", error);
        return null;
    }
};
