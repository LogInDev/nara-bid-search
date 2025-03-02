import styles from './Mytable.module.scss'
import React, { useState, useRef, useCallback, useMemo } from "react";
import { AgGridReact } from "ag-grid-react";
import { ModuleRegistry } from "ag-grid-community"; // ✅ 올바른 import
import { ClientSideRowModelModule, CsvExportModule } from "ag-grid-community"; // ✅ SetFilterModule 추가
// ✅ 모듈 경로 수정, ✅ CSV 내보내기 모듈 추가, ✅ SetFilterModule 추가

import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";

ModuleRegistry.registerModules([ClientSideRowModelModule, CsvExportModule]);

const MyTable = () => {
  const gridRef = useRef();

  const [rowData] = useState([
    { no: 1, category: "물품", bidType: "입찰공고", title: "서남센터 계측기기 교체", organization: "서울물재생시설공단", bidNumber: "R25BK00667555", amount: 138453000, announcementDate: "2025-02-24", deadline: "2025-03-04", contractMethod: "적격심사제" },
    { no: 2, category: "물품", bidType: "사전규격", title: "당동가압장 자동제어설비", organization: "경기도 군포시", bidNumber: "R25BD00024142", amount: 78100000, announcementDate: "2025-02-19", deadline: "2025-02-24", contractMethod: "적격심사제" },
    // 추가 데이터...
  ]);

  // 📌 드롭다운 필터 핸들러
  const handleFilterChange = (e) => {
    const selectedValue = e.target.value;
    if (gridRef.current) {
      gridRef.current.api.setQuickFilter(selectedValue);
    }
  };

  // 📌 '입찰유형' 드롭다운 필터 데이터
  const bidTypeOptions = [...new Set(rowData.map(item => item.bidType))];

  const [columnDefs] = useState([
    { headerName: "No", field: "no", sortable: true, filter: true },
    { headerName: "구분", field: "category", sortable: true, filter: true },
    {
      headerName: "입찰유형", field: "bidType",
      cellRenderer: (params) => params.value,  // ✅ 기본 표시 방식 유지
    },
    {
      headerName: "공고명", field: "title", sortable: true, filter: "agTextColumnFilter",
    },
    { headerName: "수요기관", field: "organization", sortable: true, filter: true },
    { headerName: "공고번호", field: "bidNumber", sortable: true, filter: "agTextColumnFilter" },
    { headerName: "기초금액", field: "amount", sortable: true, filter: "agNumberColumnFilter" },
    { headerName: "공고일", field: "announcementDate", sortable: true, filter: "agDateColumnFilter" },
    { headerName: "마감일", field: "deadline", sortable: true, filter: "agDateColumnFilter" },
    { headerName: "계약방법", field: "contractMethod", sortable: true, filter: true },
  ]);

  // 정렬방식
  const defaultColDef = useMemo(() => ({
    flex: 1, // ✅ 컬럼 크기 자동 조절
    minWidth: 100,
    filter: true, // ✅ 필터 활성화
    sortable: true, // ✅ 정렬 활성화
    resizable: true, // ✅ 컬럼 크기 조절 가능
  }));

  // 📌 엑셀 다운로드 함수
  const exportToExcel = useCallback(() => {
    if (gridRef.current) {
      gridRef.current.api.exportDataAsCsv();
    }
  }, []);

  return (
    <div className="ag-theme-alpine" style={{ height: 500, width: "100%" }}>
      {/* ✅ 필터링을 위한 드롭다운 메뉴 추가 */}
      <div style={{ marginBottom: "10px" }}>
        <label>입찰유형 필터: </label>
        <select onChange={handleFilterChange}>
          <option value="">전체</option>
          {bidTypeOptions.map((type, index) => (
            <option key={index} value={type}>{type}</option>
          ))}
        </select>
      </div>
      <button onClick={exportToExcel} style={{ marginBottom: "10px" }}>엑셀 다운로드</button>
      <AgGridReact
        ref={gridRef}
        columnDefs={columnDefs}
        rowData={rowData}
        pagination={true} // ✅ 페이징 추가
        paginationPageSize={10} // ✅ 한 페이지에 10개씩
        domLayout="autoHeight" // ✅ 자동 높이 조절
        defaultColDef={defaultColDef}
      />
    </div>
  );
};

export default MyTable;