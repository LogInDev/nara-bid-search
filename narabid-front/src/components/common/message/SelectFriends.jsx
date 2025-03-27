import axios from 'axios';
import React, { useEffect } from 'react'
import { toast } from 'react-toastify';
import { useMessageInfo } from '@/store/messageContext';
import { refreshKakaoAccessToken } from '../auth/authService';

function SelectFriends({ selectState, setFriendsInfos, handleSelectState, handleOpenFriends }) {
    const { accessToken, setAccessToken } = useMessageInfo();

    const selectFriends = async (accessToken) => {
        // 친구 목록을 axios를 이용하여 가져오기
        try {
            const urlFriendList = "https://kapi.kakao.com/v1/api/talk/friends";
            const friendResponse = await axios.get(urlFriendList, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                },
            });
            const friendsElements = friendResponse?.data?.elements || [];
            // 🔸 본인 정보 생성 (기본값)
            const selfInfo = {
                profile_nickname: "나(본인)",
                profile_img: "/icons/icon-me.png", // 기본 이미지가 있다면 경로 추가 가능
                uuid: "self",     // 나중에 uuid로 체크할 때 유용
            };

            // 🔸 친구들 정보 배열 생성
            const arrayFriendsData = Array.isArray(friendsElements)
                ? friendsElements.map(row => ({
                    profile_nickname: row.profile_nickname || "/icons/icon-me.png",
                    profile_img: row.profile_thumbnail_image || "",
                    uuid: row.uuid,
                }))
                : [];

            // 🔹 본인을 맨 앞에 두고 친구 추가
            const finalData = [selfInfo, ...arrayFriendsData];

            setFriendsInfos(finalData);
            return friendResponse?.status;
        } catch (error) {
            console.error("친구 목록 요청 실패", error.response?.data || error.message);
            return error.response?.status;
        }
    }
    useEffect(() => {
        const run = async () => {
            if (!selectState) {
                // ✅ selectState가 false면 그냥 빠져나감
                handleOpenFriends(false);
                setFriendsInfos([]);
                return;
            }

            let token = accessToken;
            if (!token || token === "undefined") {
                const newToken = await refreshKakaoAccessToken(200, setAccessToken);
                if (newToken) {
                    setAccessToken(newToken);
                    localStorage.setItem("kakao_access_token", newToken);
                    token = newToken;
                }
            }

            if (!token || token === "undefined") {
                handleSelectState(false);
                return;
            }

            const status = await selectFriends(token);
            if (status === 200) {
                handleOpenFriends(true);
            } else {
                handleSelectState(false);
                toast.error('권한이 없는 사용자입니다. 관리자에게 권한요청하세요.');
                if (status === 401) {
                    console.log(status);
                    refreshKakaoAccessToken(status, setAccessToken);
                }
            }
        };


        run();
    }, [selectState])

    return (
        <div></div>
    )
}

export default SelectFriends