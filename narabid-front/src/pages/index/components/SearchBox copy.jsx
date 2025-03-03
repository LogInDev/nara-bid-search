import { useState } from 'react';
import axios from 'axios';
import styles from './SearchBox.module.scss'
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

// import { API_BASE_URL } from '../../../../config';
import { useBidInfo } from '@/store/apiContext';

function SearchBox() {
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [searchInput, setSearchInput] = useState("");
    const [proItems, setProItems] = useState([]);
    const [bidItems, setBidItems] = useState([]);
    const [bidRegions, setBidRegions] = useState([]);
    const [bidMethods, setBidMethods] = useState([]);
    const [searchTerms, setSearchTerms] = useState([]);
    // 상세 조건 목록 mapping
    const detailCategoryMap = {
        "프로세스제어반": "4111249801",
        "계장제어장치": "3912118901",
        "유량계": "4111250101",
    };
    const regionCategoryMap = {
        "인천광역시": "28",
        "전국(제한없음)": "00"
    }
    // 상태관리 - bidInfo
    const { setBidInfos, PRE_API_URL, PRE_API_KEY, BID_API_URL, BID_API_KEY } = useBidInfo();

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
        if (text == 'terms') setSearchTerms(searchTerms.filter((_, i) => i !== index));
        else if (text == 'proItem') setProItems(proItems.filter((_, i) => i !== index));
        else if (text == 'bidItem') setBidItems(bidItems.filter((_, i) => i !== index));
        else if (text == 'bidRegion') setBidRegions(bidRegions.filter((_, i) => i !== index));
        else if (text == 'bidMethod') setBidMethods(bidMethods.filter((_, i) => i !== index));
    };

    // 서버로 검색 객체 전송
    const handleSearch = async () => {
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




        // console.log(data);

        try {
            // OpenAPI 용
            // 발주 > 사전규격 > 물품 조회
            const openApiRequests = proItems.map((item) => {
                const openApiParams = {
                    serviceKey: PRE_API_KEY,
                    pageNo: "1",  // 페이지 번호
                    numOfRows: "30",  // 한 페이지 결과 수
                    type: "json", // 타입
                    inqryDiv: "1",   // 조회구분 - 사전규격(1. 등록일시 || 2. 사전규격등록번호 || 3. 변경일시) || 입찰공고(1. 공고게시일 || 2. 개찰일시)
                    inqryBgnDt: formattedStartDateApi,  // 조회 시작일
                    inqryEndDt: formattedEndDateApi,    // 조회 종료일
                    dtilPrdctClsfcNo: item   // 세부 품명
                };

                // 개별 GET 요청 생성
                return axios.get(`${PRE_API_URL}/getPublicPrcureThngInfoThngPPSSrch`, { params: openApiParams });
            });

            // 발주 > 사전규격 > 용역 - 키워드 조회
            // const openApiRequests = searchTerms.map((item) => {
            //     const openApiParams = {
            //         serviceKey: PRE_API_KEY,
            //         pageNo: "1",  // 페이지 번호
            //         numOfRows: "30",  // 한 페이지 결과 수
            //         type: "json", // 타입
            //         inqryDiv: "1",   // 조회구분 - 사전규격(1. 등록일시 || 2. 사전규격등록번호 || 3. 변경일시) || 입찰공고(1. 공고게시일 || 2. 개찰일시)
            //         inqryBgnDt: formattedStartDateApi,  // 조회 시작일
            //         inqryEndDt: formattedEndDateApi,    // 조회 종료일
            //         prdctClsfcNoNm: item        // 키워드 
            //     };

            //     // 개별 GET 요청 생성
            //     return axios.get(`${PRE_API_URL}/getPublicPrcureThngInfoServcPPSSrch`, { params: openApiParams });
            // });
            //  모든 요청을 병렬로 실행
            const res = await Promise.all(openApiRequests);

            // 응답 처리
            console.log(res);

            const allResults = res
                .flatMap(response => response?.data?.response?.body?.items ?? []); // 모든 응답 데이터를 합침
            setBidInfos(allResults);
            console.log(allResults)



            // 입찰공고 > 물품(세부품명) 조회
            // const openApiRequests = bidRegions.flatMap(region =>
            //     bidItems.map(item => {
            //         return axios.get(`${BID_API_URL}/getBidPblancListInfoThngPPSSrch`, {
            //             params: {
            //                 serviceKey: BID_API_KEY,
            //                 pageNo: "1",  // 페이지 번호
            //                 numOfRows: "30",  // 한 페이지 결과 수
            //                 type: "json", // 타입
            //                 inqryDiv: "1",   // 조회구분 - 사전규격(1. 등록일시 || 2. 사전규격등록번호 || 3. 변경일시) || 입찰공고(1. 공고게시일 || 2. 개찰일시)
            //                 inqryBgnDt: formattedStartDateApi,  // 조회 시작일
            //                 inqryEndDt: formattedEndDateApi,    // 조회 종료일
            //                 prtcptLmtRgnCd: region,        // 참가제한지역 코드 (지역)
            //                 dtilPrdctClsfcNo: item         // 세부품명번호 (품명)
            //             }
            //         });
            //     })
            // );
            // cntrctCnclsMthdNm : 계약체결방법명 - 일반경쟁 || 제한경쟁 || 지명경쟁 || 수의계약
            // bidNtceDtlUrl : 입찰공고 상세 URL
            // ntceKindNm : 공고 상태 - '등록공고' 상태만 유효(취소공고, 변경공고 X)

            // 모든 요청을 병렬로 실행
            // const res = await Promise.all(openApiRequests);

            // // 응답 처리
            // console.log("전체 응답:", res);

            // // 모든 응답 데이터 추출
            // const allResults = res
            //     .flatMap(response => response?.data?.response?.body?.items ?? [])   // items 리스트 추출
            //     .filter(item => item.ntceKindNm === '등록공고')
            //     .filter(item => bidMethods.includes(item.cntrctCnclsMthdNm)); // bidMethods에 있는 값만 필터링

            // // 상태 업데이트
            // setBidInfos(allResults);
            // console.log("병렬 처리된 결과:", allResults);
        } catch (error) {
            console.error('검색 요청 실패', error);
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
                                            {Object.keys(detailCategoryMap).map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.checkBox__btn} ${proItems.includes(detailCategoryMap[category]) ? styles.checkBox__selected : styles.checkBox__btn}`}
                                                    onClick={() => addProItem(detailCategoryMap[category])}
                                                >
                                                    {category}
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
                                            {Object.keys(detailCategoryMap).map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.checkBox__btn} ${bidItems.includes(detailCategoryMap[category]) ? styles.checkBox__selected : styles.checkBox__btn}`}
                                                    onClick={() => addBidItem(detailCategoryMap[category])}
                                                >
                                                    {category}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <div className={styles.resultMid}>
                                        <div className={styles.resultMid__checkBox}>
                                            {Object.keys(regionCategoryMap).map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.resultMid__checkBox__btn} ${bidRegions.includes(regionCategoryMap[category]) ? styles.resultMid__checkBox__selected : styles.resultMid__checkBox__btn}`}
                                                    onClick={() => addBidRegion(regionCategoryMap[category])}
                                                >
                                                    {category}
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
                                            {["일반경쟁", "제한경쟁"].map((category) => (
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
                    <button className={styles.searchBox__btn__edit}>기본 검색 <br />조건으로 설정</button>
                    <button className={styles.searchBox__btn__default}>기본 검색 조건 <br />적용</button>
                    <button className={styles.searchBox__btn__search} onClick={handleSearch}>검색</button>
                </div>
            </div>
        </div>
    )
}

export default SearchBox