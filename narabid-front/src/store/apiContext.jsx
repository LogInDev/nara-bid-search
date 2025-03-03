import React, { createContext, useState, useContext, useEffect } from "react";
import axios from 'axios';

import { API_BASE_URL } from '../../config';

// Context 생성
const BidInfoContext = createContext();

// Provider 컴포넌트
export const BidInfoProvider = ({ children }) => {
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태관리
    const [bidInfos, setBidInfos] = useState([]);
    const [types, setTypes] = useState(new Set(["1", "2", "3"]));
    const [categories, setCategories] = useState([]);

    // OpenAPI 정보 상태관리
    const PRE_API_URL = "http://apis.data.go.kr/1230000/ao/HrcspSsstndrdInfoService";
    const PRE_API_KEY = "U7EibmMK7nMPQkycZFc6zU6uWxDMX96wMNwYwNDb0fl4Osg59xdoGsrb6pCCOWoHfTPQh0XkWqZln1YHyZtdOw==";
    const BID_API_URL = "http://apis.data.go.kr/1230000/ad/BidPublicInfoService";
    const BID_API_KEY = "U7EibmMK7nMPQkycZFc6zU6uWxDMX96wMNwYwNDb0fl4Osg59xdoGsrb6pCCOWoHfTPQh0XkWqZln1YHyZtdOw==";

    // 카테고리 정보 가져오기
    // types 배열로 변환
    const typesArray = [...types]
    const getCategory = async () => {
        try {
            const response = await axios.post(`${API_BASE_URL}/api/bids/category`, typesArray);
            console.log(response.data);


            setCategories(response.data); // 받아온 데이터를 상태에 저장
        } catch (error) {
            console.error("Error fetching category data:", error);
        }
    }

    useEffect(() => {
        getCategory();
    }, [])

    return (
        <BidInfoContext.Provider value={{ bidInfos, setBidInfos, PRE_API_URL, PRE_API_KEY, BID_API_URL, BID_API_KEY, isLoading, setIsLoading, categories, setTypes }}>
            {children}
        </BidInfoContext.Provider>
    );
};

// Context 사용을 위한 커스텀 훅
export const useBidInfo = () => useContext(BidInfoContext);
