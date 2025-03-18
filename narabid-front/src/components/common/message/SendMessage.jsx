import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { refreshKakaoAccessToken } from './KakaoService';
import { useMessageInfo } from '@/store/messageContext';
import { keys } from 'localforage';
import { loginAndGetKakaoTokens } from '../auth/KakaoAuth';
import { logoutFromKakao } from '../auth/KakaoLogout';

function SendMessage({ sendState, onSendState }) {
    const [accessToken, setAccessToken] = useState(null);
    const { messageInfos } = useMessageInfo();

    // useEffect(async () => {
    //     if (!accessToken) {
    //         const newToken = await refreshKakaoAccessToken();
    //         console.log(newToken);
    //         if (newToken) {
    //             setAccessToken(newToken);
    //         }
    //     }
    // }, [])
    const sendMessage = async (retry = 0) => {
        // let token = accessToken;
        // const maxRetry = 3; // 최대 재시도 횟수

        // if (!token) {
        //     const newToken = await refreshKakaoAccessToken();
        //     console.log(newToken);
        //     if (newToken) {
        //         setAccessToken(newToken);
        //         token = newToken;
        //     } else {
        //         console.error("토큰을 가져오지 못했습니다. 재시도 횟수:", retry + 1);
        //         if (retry < maxRetry) {
        //             // 재시도 횟수를 증가시키면서 재호출
        //             return await sendMessage(retry + 1);
        //         } else {
        //             console.error("최대 재시도 횟수를 초과했습니다.");
        //             return;
        //         }
        //     }
        // }

        // const urlMine = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
        // const urlFriendSend = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send"
        let friendIds = [];
        // const keys = ["구분", "입찰유형", "공고명", "공고번호", "기초금액", "공고일", "마감일", "계약방법", "수요기관"];
        // const content = messageInfos
        //     .map(info =>
        //         keys
        //             .filter(key => info[key] && info[key].toString().trim() !== "")
        //             .map(key => `${key}: ${info[key]}`)
        //             .join("\n")
        //     )
        //     .join("\n\n");
        // const buttonContents = messageInfos
        //     .filter(info => info["상세페이지"] && info["상세페이지"].toString().trim() !== "")
        //     .map(info => ({
        //         title: info["공고명"],
        //         link: info["상세페이지"]
        //     }));


        // 친구 목록을 axios를 이용하여 가져오기
        try {
            const urlFriendList = "https://kapi.kakao.com/v1/api/talk/friends?limit=3&order=asc";
            const friendResponse = await axios.get(urlFriendList, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                },
            });
            console.log("친구 목록:", friendResponse.data);
            friendIds = friendResponse?.data?.elements?.map(el => el.uuid);
        } catch (error) {
            console.error("친구 목록 요청 실패", error.response?.data || error.message);
            if (error.response?.status === 401) {
                // 토큰 갱신 로직 추가
                // localStorage.removeItem("kakao_access_token");  // 기존 토큰제거
                const token = await loginAndGetKakaoTokens();

                if (token) {
                    console.log("✅ 토큰 확보 성공:", token);
                    setAccessToken(token)
                    // 여기서 추가 로직 수행 (예: 메시지 전송 등)
                } else {
                    console.log("❌ 토큰 확보 실패 또는 로그인 진행 중");
                }
            }
        } finally {
            // logoutFromKakao();
        }

        // const template = {
        //     object_type: "text",
        //     text: content,
        //     button_title: "나라장터 바로가기",
        //     link: {
        //         web_url: "https://www.g2b.go.kr",
        //         mobile_web_url: "https://www.g2b.go.kr",
        //     },
        //     ...(buttonContents && {
        //         buttons: buttonContents.map(item => ({
        //             title: item.title,
        //             link: { web_url: item.link, mobile_web_url: item.link },
        //         }))
        //     })
        // };

        // try {
        //     console.log(template);
        //     // '나'에게 메시지 보내기
        //     const params = new URLSearchParams();
        //     params.append("template_object", JSON.stringify(template));

        //     const response1 = await axios.post(urlMine, params, {
        //         headers: {
        //             "Authorization": `Bearer ${accessToken}`,
        //             "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
        //         },
        //     });
        //     console.log("메시지 전송 성공", response1.data);

        //     // 친구에게 메시지 보내기
        //     // if (friendIds.length === 0) throw new Error('친구 id가 존재하지 않습니다.');
        //     // const paramsFriend = new URLSearchParams();
        //     // paramsFriend.append("template_object", JSON.stringify(template));
        //     // paramsFriend.append("receiver_uuids", JSON.stringify(friendIds));
        //     // const response2 = await axios.post(urlFriendSend, paramsFriend, {
        //     //     headers: {
        //     //         "Authorization": `Bearer ${accessToken}`,
        //     //         "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
        //     //     },
        //     // });
        //     // console.log("메시지 전송 성공", response2.data);

        // } catch (error) {
        //     console.error("메시지 전송 실패", error.response?.data || error.message);

        //     // 카카오 토큰 만료 시 재발급
        //     if (!retry && error.response?.data?.code === -401) {
        //         console.log("카카오 토큰 만료로 재발급 시도");
        //         const newToken = await refreshKakaoAccessToken();
        //         if (newToken) {
        //             setAccessToken(newToken);
        //             sendMessage(true);  // 새로운 토큰으로 재시도
        //         }
        //     }
        // }
    };

    useEffect(() => {
        const fetchToken = async () => {
            const token = await loginAndGetKakaoTokens();

            if (token) {
                console.log("✅ 토큰 확보 성공:", token);
                setAccessToken(token)
                // 여기서 추가 로직 수행 (예: 메시지 전송 등)
            } else {
                console.log("❌ 토큰 확보 실패 또는 로그인 진행 중");
            }

            onSendState(false);  // 상태를 초기화
        };

        if (sendState) {
            fetchToken();
            sendMessage();
        }
    }, [sendState, onSendState])
    // useEffect(() => {
    //     if (sendState) {
    //         sendMessage().finally(() => onSendState(false));

    //     }
    // }, [sendState])

    return (<div></div>
    );
}

export default SendMessage;
