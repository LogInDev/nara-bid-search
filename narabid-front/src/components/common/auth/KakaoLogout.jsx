import React from 'react'
import axios from 'axios';
import { KAKAO_CLIENT_ID, API_FRONT_BASE_URL } from '../../../../config';
import { useMessageInfo } from '@/store/messageContext';


export const logoutFromKakao = async () => {
    const { accessToken, setAccessToken } = useMessageInfo();
    try {
        const response = await axios.post(
            "https://kapi.kakao.com/v1/user/logout",
            {}, // POST 요청이므로 빈 객체 전달
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`, // 액세스 토큰 사용
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            }
        );

        return response?.status;
        // 추가적인 로그아웃 후 처리 로직
    } catch (error) {
        console.error('로그아웃 실패:', error.response?.data || error.message);
        return error.response?.status;
    } finally {
        // 로컬 스토리지에서 토큰 제거
        setAccessToken(null);
        localStorage.removeItem('kakao_access_token');
    }
};


