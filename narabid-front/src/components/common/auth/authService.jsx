import axios from 'axios';
import { API_BASE_URL, API_FRONT_BASE_URL, KAKAO_CLIENT_ID } from '../../../../config';

export const REDIRECT_URI = API_FRONT_BASE_URL;

export const redirectToKakaoLogin = () => {
    const authUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${encodeURIComponent(REDIRECT_URI)}&response_type=code&scope=profile_nickname,profile_image,friends,talk_message&prompt=login`;
    window.location.href = authUrl;
};

export const requestAccessTokens = async (authCode) => {
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
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            }
        );
        return response.data;
    } catch (error) {
        console.error("❌ 토큰 요청 실패:", error);
        return null;
    }
};

export const setRefreshTokenToBackend = async (tokenInfo) => {
    try {
        return await axios.post(`${API_BASE_URL}/api/auth/set-token`, tokenInfo);
    } catch (error) {
        console.error("❌ 리프레시 토큰 저장 실패:", error);
        return null;
    }
};

export const refreshKakaoAccessToken = async (status, setAccessToken) => {
    try {
        if (status !== 401) {
            const response = await axios.get(`${API_BASE_URL}/api/auth/refresh-token`);
            const code = response?.data?.code;
            if (code === -401) {
                redirectToKakaoLogin();
                return null;
            }

            const newToken = response?.data?.data?.accessToken;
            setAccessToken(newToken);
            localStorage.setItem("kakao_access_token", newToken);
            return newToken;
        } else {
            redirectToKakaoLogin();
        }
    } catch (error) {
        console.error("❌ 액세스 토큰 갱신 실패:", error);
        return null;
    }
};

export const logoutFromKakao = async (accessToken, setAccessToken) => {
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
