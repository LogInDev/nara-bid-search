import styles from './DetailDialog.module.scss'
import { useEffect, useMemo, useState } from 'react'
import toast, { toastConfig } from 'react-simple-toasts'
import 'react-simple-toasts/dist/theme/dark.css'

import { AgGridReact } from 'ag-grid-react'
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { set } from 'date-fns'
import { fetchDetailProductRequests } from '@/pages/index/apis/openAPIRequests';
import { useBidInfo } from '@/store/apiContext'


toastConfig({ theme: 'dark' })

function DetailDialog({ handleDialog }) {
    const [detailProduct, setDetailProduct] = useState("");
    const { PRODUCT_API_URL, PRODUCT_API_KEY } = useBidInfo();
    const [rowData, setRowData] = useState([]);

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
        { headerName: "세부 품목 번호", field: "category", width: 150 },
        {
            headerName: "세부 품목명", field: "bidType", flex: 1
        },
    ]);
    // 정렬방식
    const defaultColDef = useMemo(() => ({
        // filter: true, // ✅ 필터 활성화
        sortable: true, // ✅ 정렬 활성화
    }), []);
    const gridOptions = {
        domLayout: 'autoHeight',
    };

    // 세부 품목 검색
    const handleDetailSearch = async () => {
        try {
            const detailProductResponses = await fetchDetailProductRequests(detailProduct, PRODUCT_API_URL, PRODUCT_API_KEY)
            const detailProductResults = detailProductResponses?.data?.response?.body?.items ?? [];


            console.log(detailProductResponses)
            // 🔥 검색 결과를 테이블에 반영
            const formattedResults = detailProductResults.map((item, index) => ({
                no: index + 1,  // 순번
                category: item.dtilPrdctClsfcNo, // 세부품명번호
                bidType: item.prdctClsfcNoNm, // 세부품명
            }));

            setRowData(formattedResults); // rowData 업데이트

        } catch (error) {
            console.error("Error fetching detail product data:", error);
        }
    };

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
                            <span className='material-symbols-outlined' style={{ fontSize: 28 + 'px' }}>
                                close
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__body}>
                    <div className={styles.searchBar} >
                        <input type='text' placeholder='세부 품명을 입력하세요' className={styles.searchBar__input}
                            value={detailProduct} onChange={(e) => setDetailProduct(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && handleDetailSearch()}
                        />
                        <button className={styles.searchBar__btn} onClick={handleDetailSearch}>
                            <span className="material-symbols-outlined" style={{ fontSize: 28 + 'px' }}>
                                search
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__footer}>
                    <div className="ag-theme-alpine" style={{ height: 300, width: "100%" }}>
                        <AgGridReact
                            columnDefs={columnDefs}
                            rowData={rowData}
                            rowSelection="single"
                            defaultColDef={defaultColDef}
                            enableCellTextSelection={true}  // ✅ 텍스트 드래그 활성화
                            suppressRowClickSelection={true}  // ✅ 클릭 시 행 선택 방지
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default DetailDialog