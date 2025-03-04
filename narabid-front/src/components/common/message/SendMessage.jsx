import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { refreshKakaoAccessToken } from './KakaoService';

function SendMessage() {
    const [accessToken, setAccessToken] = useState(null);

    useEffect(() => {
        const fetchToken = async () => {
            const newToken = await refreshKakaoAccessToken();
            if (newToken) setAccessToken(newToken);
        };
        fetchToken();
    }, []);

    const sendMessage = async (retry = false) => {
        if (!accessToken) {
            console.log("토큰이 없습니다.");
            const newToken = await refreshKakaoAccessToken();
            if (newToken) {
                setAccessToken(newToken);
                sendMessage(true);  // 새로운 토큰으로 재시도
            }
            return;
        }

        const url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
        const weatherList = ["25°C", "60%", "5m/s"];
        const content = `날씨 정보\n기온 : ${weatherList[0]}\n습도 : ${weatherList[1]}\n바람 : ${weatherList[2]}`;

        const template = {
            object_type: "text",
            text: content,
            link: {
                web_url: "https://weather.com",
                mobile_web_url: "https://weather.com",
            },
            button_title: "날씨 상세보기",
        };

        try {
            const params = new URLSearchParams();
            params.append("template_object", JSON.stringify(template));

            const response = await axios.post(url, params, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            });

            console.log("메시지 전송 성공", response.data);
        } catch (error) {
            console.error("메시지 전송 실패", error.response?.data || error.message);

            // 카카오 토큰 만료 시 재발급
            if (!retry && error.response?.data?.code === -401) {
                console.log("카카오 토큰 만료로 재발급 시도");
                const newToken = await refreshKakaoAccessToken();
                if (newToken) {
                    setAccessToken(newToken);
                    sendMessage(true);  // 새로운 토큰으로 재시도
                }
            }
        }
    };

    return (
        <div>
            <h3>카카오 메시지 보내기</h3>
            <button onClick={() => sendMessage(false)}>메시지 보내기</button>
        </div>
    );
}

export default SendMessage;
