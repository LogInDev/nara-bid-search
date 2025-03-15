import React, { useState } from 'react'
import MyTable from './components/Mytable'
import SearchBox from './components/SearchBox'
import { useBidInfo } from '@/store/apiContext'
import SendMessage from '@/components/common/message/SendMessage';
import DetailDialog from '@/components/common/dialog/DetailDialog';

function MainPage() {
    const { bidInfos } = useBidInfo();
    const [open, setOpen] = useState(false);
    const [selectedDetail, setSelectedDetail] = useState(null);
    const [detailType, setDetailType] = useState(null); // 해당 세부 품목 추가가 사전규격(pre)인지 입찰공고(bid)인지

    // DetailDialog에서 선택한 데이터(예: { code, name })를 받아 SearchBox에 전달
    const handleSelectDetail = (data) => {
        console.log(data)
        setSelectedDetail(data);
        setOpen(false); // 선택 후 모달 닫기
    };

    return (
        <div>
            <h1>나라장터 검색 시스템</h1>
            <br />
            <SearchBox handleDialog={(param) => {
                setOpen(true);
                setDetailType(param);
            }}
                selectedDetail={selectedDetail} />
            {/* <br />
            <SendMessage /> */}
            <br />
            <MyTable />
            {open && <DetailDialog handleDialog={setOpen}
                onSelectDetail={handleSelectDetail}
                detailType={detailType}
            />}
        </div>
    )
}

export default MainPage