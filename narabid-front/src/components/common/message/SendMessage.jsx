import React, { useEffect, useMemo, useRef, useState } from 'react';
import axios from 'axios';
import { useMessageInfo } from '@/store/messageContext';
import styles from './SendMessage.module.scss';
import { toast } from 'react-toastify';

import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";

function SendMessage({ friendsInfos, handleOpenFriends }) {
    const gridRef = useRef(null);
    const { messageInfos } = useMessageInfo();
    const [selectedFriends, setSelectedFriends] = useState([]);
    const [selectedFriendIds, setSelectedFriendIds] = useState([]);
    const { accessToken } = useMessageInfo();


    // 🔹 모달이 열릴 때 body 스크롤 막기
    useEffect(() => {
        document.body.style.overflow = 'hidden';
        document.documentElement.style.overflow = 'hidden';
        return () => {
            document.body.style.overflow = 'auto';
            document.documentElement.style.overflow = 'auto';
        };
    }, []);

    const [columnDefs] = useState([
        {
            headerName: "",
            field: "checkbox",
            width: 50,
            checkboxSelection: true,
            headerCheckboxSelection: true,
            suppressSizeToFit: true, // 셀 크기 자종 조절 방지
            // 값이 true이면 배경색 변경
            cellStyle: params => {
                return params.value ? { backgroundColor: "#FEE500" } : {};
            },
            cellClassRules: {
                'my-selected-cell': params => params.value === true
            }
        },
        { headerName: 'No', field: 'no', width: 60 },
        {
            headerName: '이름', field: 'name', flex: 1, minWidth: 140,
            cellRenderer: (params) => (
                < span >
                    <img
                        src={params.data.img}
                        alt="profile"
                        style={{
                            width: "30px",
                            height: "30px",
                            marginRight: "5px",
                            verticalAlign: "middle",
                            borderRadius: '2px',
                        }}
                    />
                    {params.value}
                </span >
            ),
        },
    ]);

    // 모달창 끄기
    const closeDialog = () => {
        handleOpenFriends(false)
    }

    // 메시지 template 세팅
    const setSendDatas = () => {
        const keys = ["구분", "입찰유형", "공고명", "공고번호", "기초금액", "공고일", "마감일", "계약방법", "수요기관"];
        const content = messageInfos
            .map(info =>
                keys
                    .filter(key => info[key] && info[key].toString().trim() !== "")
                    .map(key => `${key}: ${info[key]}`)
                    .join("\n")
            )
            .join("\n\n");
        const buttonContents = messageInfos
            .filter(info => info["상세페이지"] && info["상세페이지"].toString().trim() !== "")
            .map(info => ({
                title: info["공고명"],
                link: info["상세페이지"]
            }));

        const template = {
            object_type: "text",
            text: content,
            button_title: "나라장터 바로가기",
            link: {
                web_url: "https://www.g2b.go.kr",
                mobile_web_url: "https://www.g2b.go.kr",
            },
            ...(buttonContents && {
                buttons: buttonContents.map(item => ({
                    title: item.title,
                    link: { web_url: item.link, mobile_web_url: item.link },
                }))
            })
        };
        return template;
    }

    // '나'에게 메시지 보내기 API
    const sendToMe = async (accessToken) => {
        const urlMine = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
        const template = setSendDatas();
        try {
            // '나'에게 메시지 보내기
            const params = new URLSearchParams();
            params.append("template_object", JSON.stringify(template));

            const response = await axios.post(urlMine, params, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            });
            return response?.status;
        } catch (error) {
            console.error("나에게 메시지 전송 실패", error.response?.data || error.message);
            return error.response?.status
        }
    }

    // 친구에게 메시지 보내기 API
    const sendToFriends = async (accessToken) => {
        const urlFriendSend = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send"
        const template = setSendDatas();
        try {
            // 친구에게 메시지 보내기
            if (selectedFriendIds.length === 0) throw new Error('친구 id가 존재하지 않습니다.');
            const paramsFriend = new URLSearchParams();
            paramsFriend.append("template_object", JSON.stringify(template));
            paramsFriend.append("receiver_uuids", JSON.stringify(selectedFriendIds));
            const response = await axios.post(urlFriendSend, paramsFriend, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
                },
            });
            return response?.status;
        } catch (error) {
            console.error("친구에게 메시지 전송 실패", error.response?.data || error.message);
            return error.response?.status
        }
    }

    const rowData = useMemo(() => {
        return Array.isArray(friendsInfos) ? friendsInfos.map((item, index) => ({
            no: index + 1,
            img: item.profile_img,
            name: item.profile_nickname,
            uuid: item.uuid,
        })) : [];
    }, [friendsInfos]);

    const onSelectionChanged = () => {
        const selectedRows = gridRef.current.api.getSelectedRows();
        const uuids = selectedRows.map(row => row.uuid);
        setSelectedFriends(selectedRows);
        setSelectedFriendIds(uuids);
    };

    const checkTotalRows = async () => {
        if (selectedFriendIds.length < 1) {
            toast.error('최소 1명은 선택해야합니다.');
            return;
        }
        let statusMe = null;
        let statusFriends = null;
        console.log(accessToken)
        if (!selectedFriendIds.includes("self")) { // '나'를 제외한 친구만 선택한 경우
            statusFriends = await sendToFriends(accessToken); // await 사용해서 실제 status 값을 받음
        } else { // 선택 목록에 '나'가 있는 경우
            if (selectedFriendIds.length > 1) { // '나'를 포함한 선택개수가 1보다 큰 경우 - 친구를 선택함.
                statusMe = await sendToMe(accessToken);
                statusFriends = await sendToFriends(accessToken);
            } else { // '나'를 포함한 선택개수가 1인경우 - '나'만 선택함
                statusMe = await sendToMe(accessToken);
            }
        }

        handleOpenFriends(false);
        if ((statusMe === 200 || statusMe === null) && (statusFriends === 200 || statusFriends === null)) {
            if (selectedFriendIds.length == 1) {
                toast.info(`${selectedFriends[0].name}에게 메시지를 보냈습니다.`);
            } else {
                toast.info(`${selectedFriendIds.length}명에게 메시지를 보냈습니다.`);
            }
        } else {
            toast.error("일부 전송에 실패했습니다.");
        }
    };


    return (
        <div className={styles.container}>
            <div className={styles.container__dialog}>
                <div className={styles.container__dialog__header}>
                    <div className={styles.bookmark}>
                        <button className={styles.close__button} onClick={closeDialog}>
                            {/* 구글 아이콘을 사용 */}
                            <span className='material-symbols-outlined' style={{ fontSize: 28 + 'px', color: 'black' }}>
                                close
                            </span>
                        </button>
                    </div>
                    <div>
                        <span className={styles.bookmark__authorName}>메시지 보낼 대상 선택하기</span>
                    </div>
                    <div className={styles.close}>
                        <button className={styles.sendBtn} onClick={checkTotalRows} >
                            {/* <img src="/icons/icon-send.png" /> */}
                            보내기
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__body}>

                </div>
                <div className={styles.container__dialog__footer}>
                    {friendsInfos.length == 0 ? (
                        <div className={styles.noData}>
                            ❌ 조회가능한 친구목록이 없습니다. <br />관리자에게 문의하세요.
                        </div>
                    ) : (
                        <div className="ag-theme-alpine custom-modal-grid" style={{ height: '50vh', width: '35vw', minWidth: '330px', overflowX: 'hidden', overflowY: 'hidden', borderLeft: 'none', borderRight: 'none' }}>
                            <AgGridReact
                                ref={gridRef}
                                columnDefs={columnDefs}
                                rowData={rowData || []}
                                rowSelection="multiple"
                                rowMultiSelectWithClick={true}
                                onSelectionChanged={onSelectionChanged} // 선택 변경 이벤트 핸들러
                                // defaultColDef={defaultColDef}
                                enableCellTextSelection={true}  // ✅ 텍스트 드래그 활성화
                                suppressRowClickSelection={false}  // ✅ 클릭 시 행 선택 방지
                                // domLayout={'autoHeight'} // ✅ 자동 높이 설정
                                suppressPaginationPanel={true}
                            />
                        </div>
                    )
                    }
                </div>
            </div>
        </div>
    )
}

export default SendMessage