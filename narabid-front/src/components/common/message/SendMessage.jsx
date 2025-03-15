import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { refreshKakaoAccessToken } from './KakaoService';

function SendMessage() {
    const [accessToken, setAccessToken] = useState(null);

    useEffect(() => {
        const fetchToken = async () => {
            const newToken = await refreshKakaoAccessToken();
            console.log(newToken)
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

        const urlMine = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
        const urlFriendSend = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send"
        let friendId = null;
        const weatherList = ["25°C", "60%", "5m/s"];
        const content = `날씨 정보\n기온 : ${weatherList[0]}\n습도 : ${weatherList[1]}\n바람 : ${weatherList[2]}`;

        // 친구 목록을 axios를 이용하여 가져오기
        try {
            const urlFriendList = "https://kapi.kakao.com/v1/api/talk/friends?limit=3&order=asc";
            // const friendAccessToken = "hzZfV9ie_XaslxkcqTc_USKUFIHJPCEiAAAAAQoXEC8AAAGVmFVsc4a1Lb_-w10F";
            const friendResponse = await axios.get(urlFriendList, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                },
            });
            console.log("친구 목록:", friendResponse.data);
            friendId = friendResponse?.data?.elements[0]?.uuid;
        } catch (error) {
            console.error("친구 목록 요청 실패", error.response?.data || error.message);
        }


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

            const response1 = await axios.post(urlMine, params, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            });

            if (!friendId) throw new Error('친구 id가 존재하지 않습니다.');
            console.log(friendId)
            // 친구에게 메시지 전송 시 새로운 URLSearchParams 객체 생성
            const paramsFriend = new URLSearchParams();
            paramsFriend.append("template_object", JSON.stringify(template));
            // receiver_uuids는 반드시 문자열 배열 형태로 전달해야 합니다.
            paramsFriend.append("receiver_uuids", JSON.stringify([friendId]));

            const response2 = await axios.post(urlFriendSend, paramsFriend, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            });

            console.log("메시지 전송 성공", response1.data);
            console.log("메시지 전송 성공", response2.data);
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
            <button onClick={() => sendMessage(false)}>카카오 메시지 보내기</button>
        </div>
    );
}

export default SendMessage;
