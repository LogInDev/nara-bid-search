import React, { useState } from 'react'
import MyTable from './components/Mytable'
import SearchBox from './components/SearchBox'
import { useBidInfo } from '@/store/apiContext'
import SendMessage from '@/components/common/message/SendMessage';
import DetailDialog from '@/components/common/dialog/DetailDialog';
import { MessageProvider } from '@/store/messageContext';
import styles from '@pages/index/styles/index.module.scss'
import KakaoAuthHandler from '@/components/common/auth/KakaoService';

function MainPage() {
    const [open, setOpen] = useState(false);
    const [selectedDetail, setSelectedDetail] = useState(null);
    const [detailType, setDetailType] = useState(null); // 해당 세부 품목 추가가 사전규격(pre)인지 입찰공고(bid)인지
    const [sendState, setSendState] = useState(false);

    // DetailDialog에서 선택한 데이터(예: { code, name })를 받아 SearchBox에 전달
    const handleSelectDetail = (data) => {
        console.log(data)
        setSelectedDetail(data);
        // setOpen(false); // 선택 후 모달 닫기
    };
    // 카카오 메시지 보내기 버튼 활성화
    const handleSendState = (value) => {
        setSendState(value);
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
                <SendMessage sendState={sendState} onSendState={(value) => handleSendState(value)} />
                <MyTable onSendState={(value) => handleSendState(value)} />
            </MessageProvider>
            {open && <DetailDialog handleDialog={setOpen}
                onSelectDetail={handleSelectDetail}
                detailType={detailType}
            />}
        </div>
    )
}

export default MainPage