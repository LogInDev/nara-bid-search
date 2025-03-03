import styles from './Mytable.module.scss'
import React, { useState, useRef, useCallback, useMemo } from "react";
import { AgGridReact } from "ag-grid-react";
import { ModuleRegistry } from "ag-grid-community"; // ✅ 올바른 import
import { ClientSideRowModelModule, CsvExportModule } from "ag-grid-community"; // ✅ SetFilterModule 추가
// ✅ 모듈 경로 수정, ✅ CSV 내보내기 모듈 추가, ✅ SetFilterModule 추가

import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { useBidInfo } from '@/store/apiContext';

ModuleRegistry.registerModules([ClientSideRowModelModule, CsvExportModule]);

const MyTable = () => {
  const gridRef = useRef();
  const { bidInfos } = useBidInfo();

  // 백엔드 api 용
  // const rowData = useMemo(() => bidInfos.map((item, index) => ({
  //   no: index + 1, //No 칼럼 추가
  //   category: item.category,
  //   bidType: item.bidType,
  //   title: item.title,
  //   organization: item.institution,
  //   bidNumber: item.bidNumber,
  //   amount: item.estimatedAmount,
  //   announcementDate: item.announcementDate,
  //   deadline: item.deadline,
  //   contractMethod: item.contractMethod,
  // })), [bidInfos]);

  // 사전규격 OpenAPI - 물픔(세부품명)
  const rowData = useMemo(() => bidInfos.map((item, index) => ({
    no: index + 1, // No 컬럼
    category: item.bsnsDivNm,  // 물품 || 용역
    bidType: "사전규격",
    title: item.prdctClsfcNoNm,  // 공고명
    organization: item.rlDminsttNm,  // 수요기관
    bidNumber: item.bidNtceNoList, // 공고 번호
    amount: item.asignBdgtAmt,  // 배정예산액
    announcementDate: item.rgstDt, // 등록일시
    deadline: '',
    contractMethod: '',
    fileUrl: item.specDocFileUrl1
  })), [bidInfos]);

  // 사전규격 OpenAPI - 용역 (키워드)
  // const rowData = useMemo(() => bidInfos.map((item, index) => ({
  //   no: index + 1, // No 컬럼
  //   category: item.bsnsDivNm,  // 물품 || 용역
  //   bidType: "사전규격",
  //   title: item.prdctClsfcNoNm,  // 공고명
  //   organization: item.rlDminsttNm,  // 수요기관
  //   bidNumber: item.bfSpecRgstNo, // 공고 번호
  //   amount: item.asignBdgtAmt,  // 배정예산액
  //   announcementDate: item.rgstDt, // 등록일시
  //   deadline: '',
  //   contractMethod: '',
  //   fileUrl: item.specDocFileUrl1
  // })), [bidInfos]);

  // 입찰공고 OpenAPI - 물품(세부품목)
  // const rowData = useMemo(() => bidInfos.map((item, index) => ({
  //   no: index + 1, // No 컬럼
  //   category: "물품",  // 물품 || 용역
  //   bidType: "입찰공고",
  //   title: item.bidNtceNm,  // 공고명
  //   organization: item.dminsttCd,  // 수요기관
  //   bidNumber: item.bfSpecRgstNo, // 공고 번호
  //   amount: item.asignBdgtAmt,  // 배정예산액
  //   announcementDate: item.bidBeginDt, // 입찰게시일시
  //   deadline: item.bidClseDt, // 입찰마감일시
  //   contractMethod: item.cntrctCnclsMthdNm, // 계약 방법
  //   fileUrl: item.specDocFileUrl1,  // 파일 URL
  //   pageUrl: item.bidNtceDtlUrl // 상세 페이지 URL
  // })), [bidInfos]);

  // 📌 드롭다운 필터 핸들러
  const handleFilterChange = (e) => {
    const selectedValue = e.target.value;
    if (gridRef.current) {
      gridRef.current.api.setQuickFilter(selectedValue);
    }
  };

  // 📌 '입찰유형' 드롭다운 필터 데이터
  // const bidTypeOptions = [...new Set(rowData.map(item => item.bidType))];
  const bidTypeOptions = useMemo(() => [...new Set(rowData.map(item => item.bidType))], [rowData]);


  const [columnDefs] = useState([
    { headerName: "No", field: "no", sortable: true, filter: true },
    { headerName: "구분", field: "category", sortable: true, filter: true },
    {
      headerName: "입찰유형", field: "bidType",
      cellRenderer: (params) => params.value,  // ✅ 기본 표시 방식 유지
    },
    {
      headerName: "공고명", field: "title", sortable: true, filter: "agTextColumnFilter",
      cellRenderer: (params) => {
        if (!params.data?.pageUrl) {
          return params.value;  // pageUrl이 없으면 일반 텍스트로 표시
        }
        return (
          <a href={params.data.pageUrl} target="_blank" rel="noopener noreferrer">
            {params.value}
          </a>
        );

      },
    },
    { headerName: "수요기관", field: "organization", sortable: true, filter: true },
    { headerName: "공고번호", field: "bidNumber", sortable: true, filter: "agTextColumnFilter" },
    { headerName: "기초금액", field: "amount", sortable: true, filter: "agNumberColumnFilter" },
    { headerName: "공고일", field: "announcementDate", sortable: true, filter: "agDateColumnFilter" },
    { headerName: "마감일", field: "deadline", sortable: true, filter: "agDateColumnFilter" },
    { headerName: "계약방법", field: "contractMethod", sortable: true, filter: true },
    { headerName: "첨부파일", field: "fileUrl", sortable: true, filter: true },
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
      {/* 만약 데이터가 없을 때 */}
      {rowData.length == 0 ? (
        <div className={styles.noData}>조회 가능한 데이터가 없습니다.</div>
      ) : (
        <AgGridReact
          ref={gridRef}
          columnDefs={columnDefs}
          rowData={rowData || []}
          pagination={true} // ✅ 페이징 추가
          paginationPageSize={10} // ✅ 한 페이지에 10개씩
          domLayout="autoHeight" // ✅ 자동 높이 조절
          defaultColDef={defaultColDef}
        />
      )}
    </div>
  );
};

export default MyTable;