import React, { useEffect, useRef, useState } from 'react';
import styles from './SelectFriends.module.scss';
import { toast } from 'react-toastify';

import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";

function SelectFriends({ handleSelectFriends }) {
    const gridRef = useRef(null);
    const [rowData, setRowData] = useState([]);

    // 🔹 모달이 열릴 때 body 스크롤 막기
    useEffect(() => {
        document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
        return () => {
            document.body.style.overflow = 'auto'; // 모달이 닫히면 스크롤 다시 활성화
        };
    }, []);

    const [columnDefs] = useState([
        {
            headerName: "",
            field: "checkbox",
            width: 50,
            checkboxSelection: true,
            headerCheckboxSelection: true,
            suppressSizeToFit: true, // 셀 크기 자종 조절 방지
        },
        { headerName: 'No', field: 'no', width: 60 },
        { headerName: '이름', field: 'name', flex: 1, minWidth: 140 },
    ])

    // 다이얼로그 끄기
    const closeDialog = () => {
        handleSelectFriends(false)
    }
    return (
        <div className={styles.container}>
            <div className={styles.container__dialog}>
                <div className={styles.container__dialog__header}>
                    <div className={styles.bookmark}>
                        <span className={styles.bookmark__authorName}>메시지 보낼 대상 선택하기</span>
                    </div>
                    <div className={styles.close}>
                        <button className={styles.close__button} onClick={closeDialog}>
                            {/* 구글 아이콘을 사용 */}
                            <span className='material-symbols-outlined' style={{ fontSize: 28 + 'px', color: 'black' }}>
                                close
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__body}>
                    아아아
                </div>
                <div className={styles.container__dialog_footer}>
                    <div className="ag-theme-alpine" style={{ height: '40vh', width: '45vw', overflowX: 'auto' }}>
                        <AgGridReact
                            ref={gridRef}
                            columnDefs={columnDefs}
                            rowData={rowData || []}
                            rowSelection="multiple"
                            rowMultiSelectWithClick={true}
                            // onSelectionChanged={onSelectionChanged} // 선택 변경 이벤트 핸들러
                            // defaultColDef={defaultColDef}
                            enableCellTextSelection={true}  // ✅ 텍스트 드래그 활성화
                            suppressRowClickSelection={true}  // ✅ 클릭 시 행 선택 방지
                            domLayout={'autoHeight'} // ✅ 자동 높이 설정
                            suppressPaginationPanel={true}
                        // onRowDoubleClicked={onRowDoubleClicked} // 더블 클릭 이벤트 연결
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default SelectFriends