import requests from '@pages/index/apis/requests.json';
import axios from 'axios';

// 백엔드 호출용
// // 검색 객체 구성
// const formattedStartDate = formatDate(startDate);
// const formattedEndDate = formatDate(endDate);
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

// ✅ 발주 > 사전규격 > 물품 조회 요청
export const fetchProductRequests = async (proItems, formattedStartDateApi, formattedEndDateApi, PRE_API_URL, PRE_API_KEY) => {
    return Promise.all(
        proItems.map(item =>
            axios.get(`${PRE_API_URL}/getPublicPrcureThngInfoThngPPSSrch`, {
                params: {
                    ...requests.productRequest,
                    serviceKey: PRE_API_KEY,
                    inqryBgnDt: formattedStartDateApi,
                    inqryEndDt: formattedEndDateApi,
                    dtilPrdctClsfcNo: item
                }
            })
        )
    );
};


// ✅ 발주 > 사전규격 > 용역 - 키워드 조회 요청
export const fetchProKeywordsRequests = async (proSearchTerms, formattedStartDateApi, formattedEndDateApi, PRE_API_URL, PRE_API_KEY) => {
    return Promise.all(
        proSearchTerms.map(item =>
            axios.get(`${PRE_API_URL}/getPublicPrcureThngInfoServcPPSSrch`, {
                params: {
                    ...requests.proKeywordRequest,
                    serviceKey: PRE_API_KEY,
                    inqryBgnDt: formattedStartDateApi,
                    inqryEndDt: formattedEndDateApi,
                    prdctClsfcNoNm: item
                }
            })
        )
    );
};


// ✅ 입찰공고 > 물품(세부품명) 조회 요청
export const fetchBidRequests = async (bidRegions, bidItems, formattedStartDateApi, formattedEndDateApi, BID_API_URL, BID_API_KEY) => {
    return Promise.all(
        // bidRegions.flatMap(region =>
        bidItems.map(item =>
            axios.get(`${BID_API_URL}/getBidPblancListInfoThngPPSSrch`, {
                params: {
                    ...requests.bidRequest,
                    serviceKey: BID_API_KEY,
                    inqryBgnDt: formattedStartDateApi,
                    inqryEndDt: formattedEndDateApi,
                    // prtcptLmtRgnCd: region,
                    dtilPrdctClsfcNo: item
                }
            })
        )
    );
    // );
};

// ✅ 입찰공고 > 용역 키워드 조회 요청
export const fetchBidKeywordsRequests = async (bidRegions, bidSearchTerms, formattedStartDateApi, formattedEndDateApi, BID_API_URL, BID_API_KEY) => {
    return Promise.all(
        bidRegions.flatMap(region =>
            bidSearchTerms.map(keyword =>
                axios.get(`${BID_API_URL}/getBidPblancListInfoServcPPSSrch`, {
                    params: {
                        ...requests.bidKeywordRequest,
                        serviceKey: BID_API_KEY,
                        inqryBgnDt: formattedStartDateApi,
                        inqryEndDt: formattedEndDateApi,
                        prtcptLmtRgnCd: region,
                        bidNtceNm: keyword
                    }
                })
            )
        )
    );
};


