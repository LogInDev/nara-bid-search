import React, { useEffect } from 'react'
import axios from 'axios'
import { API_BASE_URL, API_FRONT_BASE_URL } from '../../../../config'

const KAKAO_CLIENT_ID = "73b6020b74d6524073a6b7b8f7dce121";
const REDIRECT_URI = API_FRONT_BASE_URL;

// 🔹 카카오 로그인 페이지로 이동
const redirectToKakaoLogin = () => {
    const authUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${encodeURIComponent(REDIRECT_URI)}&response_type=code&scope=profile_nickname,friends,talk_message&prompt=login`;
    window.location.href = authUrl;
};

// 🔹 카카오 액세스 토큰 및 리프레시 토큰 발급
const requestAccessTokens = async (authCode) => {
    try {
        const response = await axios.post(
            "https://kauth.kakao.com/oauth/token",
            new URLSearchParams({
                grant_type: "authorization_code",
                client_id: KAKAO_CLIENT_ID,
                redirect_uri: REDIRECT_URI,
                code: authCode,
            }),
            {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8"
                }
            }
        );
        console.log("✅ 토큰 발급 응답:", response);

        const { access_token, refresh_token, refresh_token_expires_in } = response.data;
        localStorage.setItem("kakao_access_token", access_token);
        return { access_token, refresh_token, refresh_token_expires_in };
    } catch (error) {
        console.error("❌ 토큰 요청 실패:", error);
        return null;
    }
};

// 🔹 리프레시 토큰을 백엔드에 저장
const setRefreshToken = async (tokenInfo) => {
    try {
        const res = await axios.post(`${API_BASE_URL}/api/auth/set-token`, tokenInfo);
        console.log("✅ 리프레시 토큰 저장 응답:", res);
        return res;
    } catch (error) {
        console.error("❌ 리프레시 토큰 DB 저장 오류:", error);
        return null;
    }
};

// 🔹 React 컴포넌트 내부에서 실행해야 하는 `useEffect`
const KakaoAuthHandler = () => {
    useEffect(() => {
        const fetchToken = async () => {
            const urlParams = new URLSearchParams(window.location.search);
            const authCode = urlParams.get("code"); // URL에서 authCode 얻기
            const token = localStorage.getItem('kakao_access_token');
            if (token !== null && authCode) {
                try {
                    const tokenInfo = await requestAccessTokens(authCode);
                    if (tokenInfo) {
                        await setRefreshToken(tokenInfo);
                    }
                } catch (error) {
                    console.error("❌ 토큰 요청 실패:", error);
                }
            }
        };

        fetchToken();
    }, []);

    return <div></div>;
};

// 🔹 액세스 토큰 갱신
export const refreshKakaoAccessToken = async () => {
    try {
        const storedAccessToken = localStorage.getItem("kakao_access_token");
        if (storedAccessToken) {
            console.log("✅ 이미 저장된 토큰이 있습니다:", storedAccessToken);
            return storedAccessToken;
        }
        const response = await axios.get(`${API_BASE_URL}/api/auth/refresh-token`);
        console.log("✅ 액세스 토큰 갱신 응답:", response);

        const code = response?.data?.code;
        if (code === -401) {
            redirectToKakaoLogin();
        }
        return response?.data?.data?.accessToken;
    } catch (error) {
        console.error("❌ 카카오 액세스 토큰 갱신 오류:", error);
        return null;
    }
};

export default KakaoAuthHandler;