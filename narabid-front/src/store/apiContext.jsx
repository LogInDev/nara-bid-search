import React, { createContext, useState, useContext } from "react";

// Context 생성
const BidInfoContext = createContext();

// Provider 컴포넌트
export const BidInfoProvider = ({ children }) => {
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태관리
    const [bidInfos, setBidInfos] = useState([]);

    // OpenAPI 정보 상태관리
    const PRE_API_URL = "http://apis.data.go.kr/1230000/ao/HrcspSsstndrdInfoService";
    const PRE_API_KEY = "U7EibmMK7nMPQkycZFc6zU6uWxDMX96wMNwYwNDb0fl4Osg59xdoGsrb6pCCOWoHfTPQh0XkWqZln1YHyZtdOw==";
    const BID_API_URL = "http://apis.data.go.kr/1230000/ad/BidPublicInfoService";
    const BID_API_KEY = "U7EibmMK7nMPQkycZFc6zU6uWxDMX96wMNwYwNDb0fl4Osg59xdoGsrb6pCCOWoHfTPQh0XkWqZln1YHyZtdOw==";

    return (
        <BidInfoContext.Provider value={{ bidInfos, setBidInfos, PRE_API_URL, PRE_API_KEY, BID_API_URL, BID_API_KEY, isLoading, setIsLoading }}>
            {children}
        </BidInfoContext.Provider>
    );
};

// Context 사용을 위한 커스텀 훅
export const useBidInfo = () => useContext(BidInfoContext);
