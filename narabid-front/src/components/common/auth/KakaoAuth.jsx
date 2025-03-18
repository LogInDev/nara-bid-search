import axios from 'axios';

const KAKAO_CLIENT_ID = "73b6020b74d6524073a6b7b8f7dce121";
const REDIRECT_URI = "http://localhost:5173";

// 카카오 로그인 페이지로 이동
const redirectToKakaoLogin = () => {
    const authUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${encodeURIComponent(REDIRECT_URI)}&response_type=code&scope=profile_nickname,friends,talk_message&prompt=login`;
    window.location.href = authUrl;

};

// 카카오 액세스 토큰 및 리프레시 토큰 발급
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
        const { access_token, refresh_token } = response.data;
        localStorage.setItem("kakao_access_token", access_token);
        localStorage.setItem("kakao_refresh_token", refresh_token);
        return { access_token, refresh_token };
    } catch (error) {
        console.error("❌ 토큰 요청 실패:", error);
        return null;
    }
};

// 🔹 버튼 클릭 시 호출되는 메인 함수
export const loginAndGetKakaoTokens = async () => {
    const storedAccessToken = localStorage.getItem("kakao_access_token");
    const storedRefreshToken = localStorage.getItem("kakao_refresh_token");
    if (storedAccessToken && storedRefreshToken) {
        console.log("✅ 이미 저장된 토큰이 있습니다:", { access_token: storedAccessToken, refresh_token: storedRefreshToken });
        return { access_token: storedAccessToken, refresh_token: storedRefreshToken };
    }

    const urlParams = new URLSearchParams(window.location.search);
    const authCode = urlParams.get("code"); // url에서 code 얻기

    if (authCode) {
        const tokens = await requestAccessTokens(authCode);

        if (tokens) {
            console.log("✅ 새 토큰 발급 완료:", tokens);
            return tokens;
        }
    } else {
        redirectToKakaoLogin();
    }
};