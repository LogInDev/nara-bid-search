import React, { useState } from 'react'
import MyTable from './components/Mytable'
import SearchBox from './components/SearchBox'
import { useBidInfo } from '@/store/apiContext'
import SendMessage from '@/components/common/message/SendMessage';
import DetailDialog from '@/components/common/dialog/DetailDialog';
import { MessageProvider } from '@/store/messageContext';
import styles from '@pages/index/styles/index.module.scss'
import KakaoAuthHandler from '@/components/common/auth/KakaoAuthHandler';
import SelectFriends from '@/components/common/message/SelectFriends';

function MainPage() {
    const [open, setOpen] = useState(false);
    const [selectedDetail, setSelectedDetail] = useState(null);
    const [detailType, setDetailType] = useState(null); // 해당 세부 품목 추가가 사전규격(pre)인지 입찰공고(bid)인지
    const [selectState, setSelectState] = useState(false);
    const [openFriends, setOpenFriends] = useState(false);
    const [friendsInfos, setFriendsInfos] = useState([]);

    // DetailDialog에서 선택한 데이터(예: { code, name })를 받아 SearchBox에 전달
    const handleSelectDetail = (data) => {
        setSelectedDetail(data);
    };
    // 카카오 친구 목록 보여주는 모달찰 open
    const handleSelectState = (value) => {
        setSelectState(value);
    }
    const handleOpenFriends = (value) => {
        setSelectState(value);
        setOpenFriends(value);
    }

    return (
        <div>
            <h1>나라장터 검색 시스템</h1>
            <br />
            <SearchBox handleDialog={(param) => {
                setOpen(true);
                setDetailType(param);
            }}
                selectedDetail={selectedDetail} />
            <br />
            <MessageProvider>
                <KakaoAuthHandler />
                <SelectFriends selectState={selectState} handleSelectState={(value) => handleSelectState(value)}
                    setFriendsInfos={setFriendsInfos}
                    handleOpenFriends={(value) => handleOpenFriends(value)} />
                {openFriends && <SendMessage
                    friendsInfos={friendsInfos}
                    handleOpenFriends={(value) => handleOpenFriends(value)}
                />}
                <MyTable handleSelectState={(value) => handleSelectState(value)} />
            </MessageProvider>
            {open && <DetailDialog handleDialog={setOpen}
                onSelectDetail={handleSelectDetail}
                detailType={detailType}
            />}
        </div>
    )
}

export default MainPage