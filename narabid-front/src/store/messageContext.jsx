import React, { createContext, useContext, useEffect, useState } from "react";

// Context 생성
const MessageInfoContext = createContext();

// Provider 컴포넌트
export const MessageProvider = ({ children }) => {
    const [selectedRows, setSelectedRows] = useState([]);
    const [messageInfos, setMessageInfos] = useState([]);

    useEffect(() => {
        console.log(selectedRows);
        const newMessageInfos = selectedRows.map(row => ({
            '구분': row.category,
            '입찰유형': row.bidType,
            '공고명': row.title,
            '공고번호': row.bidNumber,
            '기초금액': row.amount ? Number(row.amount).toLocaleString() + "원" : "",
            '공고일': row.announcementDate,
            '마감일': row.deadline,
            '계약방법': row.contractMethod,
            '수요기관': row.organization,
            '상세페이지': row.pageUrl,
        }));
        setMessageInfos(newMessageInfos);
    }, [selectedRows])


    return (
        <MessageInfoContext.Provider value={{ selectedRows, setSelectedRows, messageInfos }}>
            {children}
        </MessageInfoContext.Provider>
    )
};

// Context 사용을 위한 커스텀 훅
export const useMessageInfo = () => useContext(MessageInfoContext)