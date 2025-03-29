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
import { useSearchParams } from 'react-router-dom';

function MainPage() {
    const [open, setOpen] = useState(false);
    const [selectedDetail, setSelectedDetail] = useState(null);
    const [detailType, setDetailType] = useState(null); // 해당 세부 품목 추가가 사전규격(pre)인지 입찰공고(bid)인지
    const [selectState, setSelectState] = useState(false);
    const [openFriends, setOpenFriends] = useState(false);
    const [friendsInfos, setFriendsInfos] = useState([]);

    // 카카오 공유 메시지 링크 검색 API
    const [params] = useSearchParams();
    const bidNumber = params.get('bidNumber');   // 공고번호
    const bidType = params.get('bidType');       // 입찰유형
    const category = params.get('category');     // 구분(세부품목)
    const hasSearchParams = bidNumber && bidType && category;



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
    const headerImages = new Array(24).fill("/gray-nivus.png"); // 10개 복사

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <div className={styles.twelve}>
                    {/* <img className={styles.header__img} src="/nivus.png" alt="nivus" /> */}
                    <h1 className={styles.title}>&nbsp;&nbsp;&nbsp;&nbsp;입찰정보 통합검색</h1>
                </div>
            </div>
            <br />
            <SearchBox handleDialog={(param) => {
                setOpen(true);
                setDetailType(param);
            }}
                selectedDetail={selectedDetail}
                {...(hasSearchParams ? { searchParams: { bidNumber, bidType, category } } : {})}
            />
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