import { use, useEffect, useRef, useState } from 'react';
import axios from 'axios';
import styles from './SearchBox.module.scss'
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import toast, { toastConfig } from 'react-simple-toasts'
import 'react-simple-toasts/dist/theme/dark.css'

toastConfig({ theme: 'dark' });

import { useBidInfo } from '@/store/apiContext';

function SearchBox() {
    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
    oneMonthAgo.setDate(oneMonthAgo.getDate() + 1);
    // 상태관리 - bidInfo
    const { setBidInfos, BASE_API_URL, PRE_API_URL, PRE_API_KEY, BID_API_URL, BID_API_KEY, setIsLoading, categories, setCategories } = useBidInfo();
    // 상세 검색
    const [startDate, setStartDate] = useState(oneMonthAgo);
    const [endDate, setEndDate] = useState(today);
    const [searchInput, setSearchInput] = useState("");
    const [proItems, setProItems] = useState([]);
    const [bidItems, setBidItems] = useState([]);
    const [bidRegions, setBidRegions] = useState([]);
    const [bidMethods, setBidMethods] = useState([]);
    const [searchTerms, setSearchTerms] = useState([]);
    //카테고리
    const [preDetailCategory, setPreDetailCategory] = useState([{
        "프로세스제어반": "4111249801",
        "계장제어장치": "3912118901",
        "유량계": "4111250101",
    }]);
    const [bidDetailCategory, setBidDetailCategory] = useState([{
        "프로세스제어반": "4111249801",
        "계장제어장치": "3912118901",
        "유량계": "4111250101",
    }]);
    const [regionCategory, setRegionCategory] = useState([{
        "인천광역시": "28",
        "전국(제한없음)": "00"
    }]);
    const [bidMethodCategory, setBidMethodCategory] = useState(["일반경쟁", "제한경쟁"]);

    const isInitialized = useRef(false); // 처음 실행 여부 추적
    const isSearched = useRef(false); // handleSearch가 실행되었는지 여부

    useEffect(() => {

        if (categories.length !== 0) {
            setPreDetailCategory((categories.preDetailProducts || []).reduce((acc, item) => {
                const [key, value] = item.split(":");
                acc[key] = value;
                return acc;
            }, {})
            );
            setBidDetailCategory((categories.bidDetailProducts || []).reduce((acc, item) => {
                const [key, value] = item.split(":")
                acc[key] = value;
                return acc;
            }, {}));
            setRegionCategory((categories.restrictRegions || []).reduce((acc, item) => {
                const [key, value] = item.split(":")
                acc[key] = value;
                return acc;
            }, {}));
            setBidMethodCategory(categories.contractMethods || []);
            setSearchTerms(categories.keywords || []);

            const fetchAndSearch = async () => {
                await applyDefault(); // ✅ applyDefault() 완료될 때까지 대기
                isInitialized.current = true; // ✅ applyDefault() 실행 완료 표시
            };

            fetchAndSearch();

        }
    }, [categories]);

    useEffect(() => {
        if (isInitialized.current && !isSearched.current) {
            handleSearch(); // ✅ 한 번만 실행
            isSearched.current = true; // ✅ 이후 실행 방지
        }
    }, [isInitialized.current]);

    // 날짜 포맷 yyyyMMdd 형식으로 변환하는 함수
    const formatDate = (date, isEnd = null) => {
        if (!date) return "";
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        if (isEnd === true) return `${year}${month}${day}2359`; // 종료일 (HHmm 붙이기)
        if (isEnd === false) return `${year}${month}${day}0000`; // 시작일 (HHmm 붙이기)
        return `${year}${month}${day}`;
    }


    // 체크박스 선택/해제
    const toggleCategory = (category, setItems) => {
        setItems((prev) =>
            prev.includes(category)
                ? prev.filter((item) => item !== category) // 체크 해제 시 제거
                : [...prev, category] // 체크 시 추가
        );
    };


    // 중복 체크 후 상태 업데이트 함수
    const addItem = (input, setItems, setInput) => {
        if (input.trim() !== "") {
            setItems(prev => (prev.includes(input) ? prev : [...prev, input]));
            setInput("");
        }
    };

    // 검색어 추가
    const addSearchTerm = () => addItem(searchInput, setSearchTerms, setSearchInput);
    const addBidItem = (category) => toggleCategory(category, setBidItems);
    const addProItem = (category) => toggleCategory(category, setProItems);
    const addBidRegion = (category) => toggleCategory(category, setBidRegions);
    const addBidMethod = (category) => toggleCategory(category, setBidMethods);

    // enter로 검색어 추가
    const handleKeyDown = (e) => {
        if (e.nativeEvent.isComposing) return;
        if (e.key === 'Enter') {
            e.preventDefault(); // 기본 이벤트 방지 (특정 환경에서 중복 실행 방지)
            addSearchTerm()
        }
    }

    // 검색어 삭제
    const removeSearchTerm = (text, index) => {
        // if (text == 'terms') setSearchTerms(searchTerms.filter((_, i) => i !== index));
        if (text === 'terms') {
            setSearchTerms(prevTerms => prevTerms.filter((_, i) => i !== index));
        }
        else if (text == 'proItem') setProItems(proItems.filter((_, i) => i !== index));
        else if (text == 'bidItem') setBidItems(bidItems.filter((_, i) => i !== index));
        else if (text == 'bidRegion') setBidRegions(bidRegions.filter((_, i) => i !== index));
        else if (text == 'bidMethod') setBidMethods(bidMethods.filter((_, i) => i !== index));
    };

    // 기본 검색 조건 설정
    const handleDetail = async () => {
        const mappedBidRegions = bidRegions.map((item) => {
            const matchdRegion = Object.keys(regionCategory).find(
                (key) => regionCategory[key] === item
            );
            return matchdRegion;
        })
        const reqData = {
            "keywords": searchTerms,
            "bidDetailProducts": bidItems,
            "preDetailProducts": proItems,
            "restrictRegions": mappedBidRegions,
            "contractMethods": bidMethods,
        }

        try {

            const response = await axios.post(`${BASE_API_URL}/api/bids/updateCategory`, reqData);
        } catch (error) {
            console.error('기본 검색 조건 적용 실패', error);
        } finally {
            alert('기본 검색 조건이 변경되었습니다. 👍');
        }
    }

    // 기본 검색조건 적용
    const applyDefault = async () => {

        try {

            const response = await axios.get(`${BASE_API_URL}/api/bids/selectedCategory`);
            const resData = response.data;

            setSearchTerms(resData.keywords);
            setBidItems(resData.bidDetailProducts.map(item => item.split(":")[1]));
            setProItems(resData.preDetailProducts.map(item => item.split(":")[1]));
            setBidRegions(resData.restrictRegions.map(item => item.split(":")[1]));
            setBidMethods(resData.contractMethods);


        } catch (error) {
            console.error('기본 검색 조건 조회 실패', error);
        }
    }

    // 서버로 검색 객체 전송
    const handleSearch = async () => {
        setIsLoading(true); // 로딩 시작
        // 날짜 포맷 수정
        const formattedStartDate = formatDate(startDate);
        const formattedEndDate = formatDate(endDate);
        const formattedStartDateApi = formatDate(startDate, false);
        const formattedEndDateApi = formatDate(endDate, true);

        // 백엔드 호출용
        // // 검색 객체 구성
        // const data = {
        //     startDate: formattedStartDate,
        //     endDate: formattedEndDate,
        //     proItems,
        //     bidItems,
        //     bidRegions,
        //     bidMethods,
        //     searchTerms,
        // };
        // const res = await axios.post(`${API_BASE_URL}/api/bids/search`, data, {  
        // });





        try {
            // OpenAPI 용
            // 🔹 1. 발주 > 사전규격 > 물품 조회 요청
            const productRequests = proItems.map((item) => {
                return axios.get(`${PRE_API_URL}/getPublicPrcureThngInfoThngPPSSrch`, {
                    params: {
                        serviceKey: PRE_API_KEY,
                        pageNo: "1",
                        numOfRows: "100",
                        type: "json",
                        inqryDiv: "1",
                        inqryBgnDt: formattedStartDateApi,
                        inqryEndDt: formattedEndDateApi,
                        dtilPrdctClsfcNo: item
                    }
                });
            });

            // 🔹 2. 발주 > 사전규격 > 용역 - 키워드 조회 요청
            const serviceRequests = searchTerms.map((item) => {
                return axios.get(`${PRE_API_URL}/getPublicPrcureThngInfoServcPPSSrch`, {
                    params: {
                        serviceKey: PRE_API_KEY,
                        pageNo: "1",
                        numOfRows: "100",
                        type: "json",
                        inqryDiv: "1",
                        inqryBgnDt: formattedStartDateApi,
                        inqryEndDt: formattedEndDateApi,
                        prdctClsfcNoNm: item
                    }
                });
            });

            // 🔹 3. 입찰공고 > 물품(세부품명) 조회 요청
            const bidRequests = bidRegions.flatMap(region =>
                bidItems.map(item => {
                    return axios.get(`${BID_API_URL}/getBidPblancListInfoThngPPSSrch`, {
                        params: {
                            serviceKey: BID_API_KEY,
                            pageNo: "1",
                            numOfRows: "100",
                            type: "json",
                            inqryDiv: "1",
                            inqryBgnDt: formattedStartDateApi,
                            inqryEndDt: formattedEndDateApi,
                            prtcptLmtRgnCd: region,
                            dtilPrdctClsfcNo: item
                        }
                    });
                })
            );

            // 🔹 모든 요청을 병렬 실행
            const [productResponses, serviceResponses, bidResponses] = await Promise.all([
                Promise.all(productRequests),
                Promise.all(serviceRequests),
                Promise.all(bidRequests)
            ]);

            // 🔹 사전규격(물품, 용역) 데이터에 구분값 추가
            const productResults = productResponses.flatMap(response =>
                (response?.data?.response?.body?.items ?? []).map(item => ({
                    ...item,
                    type: 2 // ✅ 사전규격 - 물품
                }))
            );

            const serviceResults = serviceResponses.flatMap(response =>
                (response?.data?.response?.body?.items ?? []).map(item => ({
                    ...item,
                    type: 1 // ✅ 사전규격 - 용역
                }))
            );

            // 🔹 입찰공고 데이터에 구분값 추가 (필터 적용 후)
            const bidResults = bidResponses
                .flatMap(response => response?.data?.response?.body?.items ?? [])
                .filter(item => item.ntceKindNm === '등록공고') // '등록공고' 상태만 포함
                .filter(item => bidMethods.includes(item.cntrctCnclsMthdNm)) // 계약 방법 필터링
                .map(item => ({
                    ...item,
                    type: 3 // ✅ 입찰공고 - 물품
                }));
            // 🔹 모든 데이터를 합쳐서 상태 업데이트
            const allResults = [...productResults, ...serviceResults, ...bidResults];

            // 상태 업데이트
            setBidInfos(allResults);
        } catch (error) {
            console.error('검색 요청 실패', error);
        } finally {
            setIsLoading(false);
        }

    }


    return (
        <div className={styles.searchBox}>
            <div className={styles.searchBox__detail}>
                {/* 검색 일자 설정 */}
                <div className={styles.dateBox}>
                    <div className={styles.dateBox__start}>
                        <label className={styles.dateBox__label}>조회 시작일</label>
                        <DatePicker
                            selected={startDate}
                            onChange={setStartDate}
                            dateFormat="yyyy/MM/dd"
                            className="border p-2 rounded w-full"
                            placeholderText="YYYY/MM/DD"
                        />
                    </div>
                    <div className={styles.dateBox__end}>
                        <label className={styles.dateBox__label}>조회 종료일</label>
                        <DatePicker
                            selected={endDate}
                            onChange={setEndDate}
                            dateFormat="yyyy/MM/dd"
                            className="border p-2 rounded w-full"
                            placeholderText="YYYY/MM/DD"
                        />
                    </div>
                </div>
                <div className={styles.wrapDetail}>
                    {/* 테이블생성 */}
                    <table className={styles.table}>
                        <tbody>
                            <tr>
                                <td rowSpan={3} className={styles.table__title}>사전규격 <br />&nbsp;- 일반용역, 기술용역</td>
                                <td className={styles.table__searchBar}>
                                    <div className={styles.searchBar}>
                                        <input type='text' placeholder='검색어를 입력하세요' className={styles.searchBar__input}
                                            value={searchInput}
                                            onChange={(e) => setSearchInput(e.target.value)}
                                            onKeyDown={handleKeyDown} />
                                        <button onClick={addSearchTerm} className={styles.searchBar__btn}>
                                            <img src='src/assets/icons/icon-plus.png' alt="" />
                                        </button>
                                    </div>
                                </td>
                                <td></td>
                            </tr>
                            <tr>
                                <td rowSpan={2} colSpan={2} className={styles.table__searchResult} style={{ backgroundColor: "main.$color-gray-100" }}>
                                    <div className={styles.resultBox}>
                                        <div className={styles.searchBar__results}>
                                            {/* 검색어에 추가 결과 표시부분 */}
                                            {searchTerms.map((term, index) => (
                                                <div key={index} className={styles.searchBar__results__tag}>
                                                    {term}
                                                    <button className={styles.searchBar__search__btn}
                                                        onClick={() => removeSearchTerm('terms', index)}>
                                                        <img src='src/assets/icons/icon-cross.png' alt="" />
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr></tr>
                            <tr>
                                <td rowSpan={3} className={styles.table__title} >사전규격 - 물품</td>
                                <td className={styles.table__wrapMid} colSpan={2}>
                                    {/* 세부품목선택 */}
                                    <div className={styles.table__title__mid}>세부 품목</div>
                                </td>
                            </tr>
                            <tr>
                                <td rowSpan={2} colSpan={2} className={styles.table__checkResult}>
                                    <div className={styles.resultBox}>
                                        <div className={styles.checkBox}>
                                            {Object.entries(preDetailCategory).map(([key, value]) => (
                                                <button
                                                    key={value}
                                                    className={`${styles.checkBox__btn} ${proItems.includes(value) ? styles.checkBox__selected : styles.checkBox__btn}`}
                                                    onClick={() => addProItem(value)}
                                                >
                                                    {key}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr></tr>
                            <tr>
                                <td rowSpan={4} className={styles.table__title}>입찰공고 - 물품</td>
                                <td className={styles.table__wrapMid}>
                                    {/* 세부품목선택 */}
                                    <div className={styles.table__title__mid}>세부 품목</div>
                                </td>
                                <td className={styles.table__wrapMid}>
                                    <div className={styles.table__title__mid}>제한지역</div>
                                </td>
                            </tr>
                            <tr>
                                <td rowSpan={3}>
                                    <div className={styles.resultBox}>
                                        <div className={styles.checkBox}>
                                            {Object.entries(bidDetailCategory).map(([key, value]) => (
                                                <button
                                                    key={key}
                                                    className={`${styles.checkBox__btn} ${bidItems.includes(value) ? styles.checkBox__selected : styles.checkBox__btn}`}
                                                    onClick={() => addBidItem(value)}
                                                >
                                                    {key}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <div className={styles.resultMid}>
                                        <div className={styles.resultMid__checkBox}>

                                            {
                                                Object.entries(regionCategory).map(([key, value]) => (
                                                    <button
                                                        key={key}
                                                        className={`${styles.resultMid__checkBox__btn} ${bidRegions.includes(value) ? styles.resultMid__checkBox__selected : styles.resultMid__checkBox__btn}`}
                                                        onClick={() => addBidRegion(value)}
                                                    >
                                                        {key}
                                                    </button>
                                                ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td className={styles.table__wrapMid}>
                                    <div className={styles.table__title__mid}>계약방법</div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div className={styles.resultMid}>
                                        <div className={styles.resultMid__checkBox}>
                                            {bidMethodCategory.map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.resultMid__checkBox__btn} ${bidMethods.includes(category) ? styles.resultMid__checkBox__selected : styles.resultMid__checkBox__btn}`}
                                                    onClick={() => addBidMethod(category)}
                                                >
                                                    {category}

                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div className={styles.searchBox__btn}>
                <div className={styles.searchBox__wrapBtn}>
                    {/* 버튼 생성 */}
                    <button className={styles.searchBox__btn__edit} onClick={handleDetail}>기본 검색 <br />조건으로 설정</button>
                    <button className={styles.searchBox__btn__default} onClick={applyDefault}>기본 검색 조건 <br />적용</button>
                    <button className={styles.searchBox__btn__search} onClick={handleSearch}>검색</button>
                </div>
            </div>
        </div >
    )
}

export default SearchBox