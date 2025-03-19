import React from 'react'

import axios from 'axios';

export const logoutFromKakao = async () => {
    const accessToken = localStorage.getItem('kakao_access_token');
    console.log(accessToken);
    if (!accessToken) {
        console.error('액세스 토큰이 없습니다.');
        return;
    }

    try {
        const response = await axios.post(
            'https://kapi.kakao.com/v1/user/logout',
            {},
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
                },
            }
        );

        console.log('로그아웃 성공:', response.data);
        // 로컬 스토리지에서 토큰 제거
        localStorage.removeItem('kakao_access_token');
        // 추가적인 로그아웃 후 처리 로직
    } catch (error) {
        console.error('로그아웃 실패:', error.response?.data || error.message);
    }
};
