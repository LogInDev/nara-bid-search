
import styles from './DetailDialog.module.scss'
import { useEffect, useMemo, useRef, useState } from 'react'

import { AgGridReact } from 'ag-grid-react'
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { set } from 'date-fns'
import { fetchDetailProductRequests } from '@/pages/index/apis/openAPIRequests';
import { useBidInfo } from '@/store/apiContext'




function DetailDialog({ handleDialog, onSelectDetail, detailType }) {
    const [detailProduct, setDetailProduct] = useState("");
    const { PRODUCT_API_URL, PRODUCT_API_KEY } = useBidInfo();
    const [rowData, setRowData] = useState([]);
    // const [totalCount, setTotalCount] = useState(0);
    const [pageSize] = useState(7);
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [isPaginationVisible, setIsPaginationVisible] = useState(false);

    const gridRef = useRef(null); // Ag-Grid API 사용을 위한 ref

    // 🔹 모달이 열릴 때 body 스크롤 막기
    useEffect(() => {
        document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
        return () => {
            document.body.style.overflow = 'auto'; // 모달이 닫히면 스크롤 다시 활성화
        };
    }, []);

    // 다이얼로그 끄기
    const closeDialog = () => {
        handleDialog(false)
    }

    const [columnDefs] = useState([
        { headerName: "No", field: "no", width: 60 },
        { headerName: "세부 품목 번호", field: "category", width: 140 },
        {
            headerName: "세부 품목명", field: "bidType", flex: 1, minWidth: 210
        },
        {
            headerName: "",
            field: "action",
            width: 80,
            cellRenderer: (params) => {
              return (
                <button
                  className={`${styles.addButton}`}
                  onClick={() => {
                    const { category, bidType } = params.data;
                    if (onSelectDetail) {
                      onSelectDetail({ code: category, name: bidType, type: detailType });
                    }
                  }}
                >
                  추가
                </button>
              );
            }
          },
    ]);
    // 정렬방식
    const defaultColDef = useMemo(() => ({
        // filter: true, // ✅ 필터 활성화
        sortable: true, // ✅ 정렬 활성화
    }), []);

    const fetchAndSearch = async (page, size) => {
        try {
            const detailProductResponses = await fetchDetailProductRequests(detailProduct, page, size, PRODUCT_API_URL, PRODUCT_API_KEY)
            const detailProductResults = detailProductResponses?.data?.response?.body?.items ?? [];
            const total = detailProductResponses?.data?.response?.body?.totalCount || 0;

            setTotalPages(Math.ceil(total / size));
            setCurrentPage(page);
            setIsPaginationVisible(total > size); // totalCount가 pageSize보다 크면 UI 표시

            // 🔥 검색 결과를 테이블에 반영
            const formattedResults = detailProductResults.map((item, index) => ({
                no: (page - 1) * size + (index + 1),  // 순번
                category: item.dtilPrdctClsfcNo, // 세부품명번호
                bidType: item.prdctClsfcNoNm, // 세부품명
            }));

            setRowData(formattedResults); // rowData 업데이트

        } catch (error) {
            console.error("Error fetching detail product data:", error);
        }
    };

    // 페이지네이션
    const handlePrevPage = () => {
        if (currentPage > 1) {
            fetchAndSearch(currentPage - 1, pageSize);
        }
    };

    const handleNextPage = () => {
        if (currentPage < totalPages) {
            fetchAndSearch(currentPage + 1, pageSize);
        }
    };

    // 세부 품목 결과 더블 클릭 이벤트 핸들러 : 선택된 행의 데이터를 index(부모)에 전달
    const onRowDoubleClicked = (params) => {
        const { category, bidType } = params.data;
        if (onSelectDetail) {
            if (detailType) onSelectDetail({ code: category, name: bidType, type: detailType })
        }
    }


    return (
        <div className={styles.container}>
            <div className={styles.container__dialog}>
                <div className={styles.container__dialog__header}>
                    <div className={styles.close}>
                        <span className={styles.close__authorName}>세부 품목 추가</span>
                    </div>
                    <div className={styles.bookmark}>
                        <button className={styles.close__button} onClick={closeDialog}>
                            {/* 구글 아이콘을 사용 */}
                            <span className='material-symbols-outlined' style={{ fontSize: 28 + 'px', color: 'white' }}>
                                close
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__body}>
                    <div className={styles.searchBar} >
                        <input type='text' placeholder='세부 품명을 입력하세요' className={styles.searchBar__input}
                            value={detailProduct} onChange={(e) => setDetailProduct(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && fetchAndSearch(1, pageSize)}
                        />
                        <button className={styles.searchBar__btn} onClick={() => fetchAndSearch(1, pageSize)}>
                            <span className="material-symbols-outlined" style={{ fontSize: 28 + 'px', padding: 2 + 'px' }}>
                                search
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__footer}>
                    <div className="ag-theme-alpine" style={{ height: '40vh', width: '45vw', overflowX: 'auto' }}>
                        <AgGridReact
                            columnDefs={columnDefs}
                            rowData={rowData}
                            rowSelection="single"
                            defaultColDef={defaultColDef}
                            enableCellTextSelection={true}  // ✅ 텍스트 드래그 활성화
                            suppressRowClickSelection={true}  // ✅ 클릭 시 행 선택 방지
                            // domLayout={'autoHeight'} // ✅ 자동 높이 설정
                            suppressPaginationPanel={true}
                            pagination={false}  // 기본 페이지네이션 숨김
                            onRowDoubleClicked={onRowDoubleClicked} // 더블 클릭 이벤트 연결
                        />
                    </div>

                    {/* ✅ 커스텀 페이지네이션 UI */}
                    {isPaginationVisible && (
                        <div className={styles.pagination}>
                            <button onClick={() => fetchAndSearch(1, pageSize)} disabled={currentPage === 1}>«</button>
                            <button onClick={handlePrevPage} disabled={currentPage === 1}>‹</button>
                            <span> {currentPage} / {totalPages} </span>
                            <button onClick={handleNextPage} disabled={currentPage === totalPages}>›</button>
                            <button onClick={() => fetchAndSearch(totalPages, pageSize)} disabled={currentPage === totalPages}>»</button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default DetailDialog