import React, { useEffect, useState } from 'react';
import { useMessageInfo } from '@/store/messageContext';
import {
    requestAccessTokens,
    setRefreshTokenToBackend,
} from '@components/common/auth/authService';

const KakaoAuthHandler = () => {
    const { accessToken, setAccessToken } = useMessageInfo();

    const [authCode, setAuthCode] = useState(() => {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get("code");
    });

    useEffect(() => {
        const fetchToken = async () => {
            if (!authCode) return;

            const stored = accessToken || localStorage.getItem("kakao_access_token");

            if (!stored || stored === "undefined") {
                const tokenInfo = await requestAccessTokens(authCode);
                if (tokenInfo) {
                    const { access_token, refresh_token, refresh_token_expires_in } = tokenInfo;

                    setAccessToken(access_token);
                    localStorage.setItem("kakao_access_token", access_token);

                    await setRefreshTokenToBackend({
                        access_token,
                        refresh_token,
                        refresh_token_expires_in,
                    });
                }
            }
        };

        fetchToken();
    }, [authCode]);

    return <div />;
};

export default KakaoAuthHandler;
