import styles from './Mytable.module.scss'
import React, { useState, useRef, useCallback, useMemo } from "react";
import { toast } from 'react-toastify';
import { AgGridReact } from "ag-grid-react";
import { ModuleRegistry } from "ag-grid-community";
import { ClientSideRowModelModule, CsvExportModule } from "ag-grid-community"; // ✅ SetFilterModule 추가

import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { useBidInfo } from '@/store/apiContext';
import CustomTooltip from '@/components/common/tooltip/CustomTooltip';
import Loading from './Loading';
import { useMessageInfo } from '@/store/messageContext';
import * as XLSX from 'xlsx';
import ExcelJS from "exceljs";
import { saveAs } from "file-saver";
import CommonTooltip from '@/components/common/tooltip/CommonTooltip';

ModuleRegistry.registerModules([ClientSideRowModelModule, CsvExportModule]);

const MyTable = ({ onSendState }) => {
  const gridRef = useRef(null);
  const { bidInfos, isLoading } = useBidInfo();
  const { selectedRows, setSelectedRows } = useMessageInfo();

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

  const rowData = useMemo(() => {
    if (!Array.isArray(bidInfos)) {
      console.error("bidInfos가 배열이 아닙니다:", bidInfos);
      return []; // bidInfos가 유효하지 않으면 빈 배열 반환
    }

    return bidInfos.flatMap((item, index) => {
      const results = [];

      // 파일 URL 목록 추출
      const fileUrls = [item.specDocFileUrl1, item.specDocFileUrl2, item.specDocFileUrl3, item.specDocFileUrl4, item.specDocFileUrl5, item.specDocFileUrl6, item.specDocFileUrl7, item.specDocFileUrl8, item.specDocFileUrl9, item.specDocFileUrl10].filter(url => url);
      const preFileUrls = [item.specDocFileUrl1, item.specDocFileUrl2, item.specDocFileUrl3, item.specDocFileUrl4, item.specDocFileUrl5].filter(url => url);

      // 🔹 입찰공고 데이터 추가 - type : 3
      if (item.type === 4) {
        // 파일 목록 생성 (파일명과 URL을 매핑)
        const fileList = [];
        for (let i = 1; i <= 10; i++) {
          const fileName = item[`ntceSpecFileNm${i}`];
          const fileUrl = item[`ntceSpecDocUrl${i}`];

          if (fileName && fileName.trim() !== "" && fileUrl && fileUrl.trim() !== "") {
            fileList.push({ name: fileName, url: fileUrl });
          }
        }
        results.push({
          no: index + 1,
          category: item.srvceDivNm,  // 물품 || 용역
          bidType: "입찰공고",
          title: item.bidNtceNm,
          organization: item.dminsttNm,
          bidNumber: item.bidNtceNo,
          amount: item.asignBdgtAmt,
          announcementDate: item.bidNtceDt,
          // announcementDate: item.bidBeginDt,
          deadline: item.bidClseDt,
          contractMethod: item.cntrctCnclsMthdNm,
          pageUrl: item.bidNtceDtlUrl,
          fileList, // 파일 목록 저장
        });
      }

      // 🔹 입찰공고 데이터 추가 - type : 3
      if (item.type === 3) {
        // 파일 목록 생성 (파일명과 URL을 매핑)
        const fileList = [];
        for (let i = 1; i <= 10; i++) {
          const fileName = item[`ntceSpecFileNm${i}`];
          const fileUrl = item[`ntceSpecDocUrl${i}`];

          if (fileName && fileName.trim() !== "" && fileUrl && fileUrl.trim() !== "") {
            fileList.push({ name: fileName, url: fileUrl });
          }
        }
        results.push({
          no: index + 1,
          category: "물품",  // 물품 || 용역
          bidType: "입찰공고",
          title: item.bidNtceNm,
          organization: item.dminsttNm,
          bidNumber: item.bidNtceNo,
          amount: item.asignBdgtAmt,
          announcementDate: item.bidNtceDt,
          deadline: item.bidClseDt,
          contractMethod: item.cntrctCnclsMthdNm,
          pageUrl: item.bidNtceDtlUrl,
          fileList, // 파일 목록 저장
        });
      }

      // 🔹 사전규격 - 물품 데이터 추가 - type : 2
      if (item.type === 2) {
        const fileList = [];
        for (let i = 1; i <= 5; i++) {
          const fileName = `첨부파일${i}`;
          const fileUrl = item[`specDocFileUrl${i}`];

          if (fileName && fileName.trim() !== "" && fileUrl && fileUrl.trim() !== "") {
            fileList.push({ name: fileName, url: fileUrl });
          }
        }

        results.push({
          no: index + 1,
          category: item.bsnsDivNm,
          bidType: "사전규격",
          title: item.prdctClsfcNoNm,
          organization: item.rlDminsttNm,
          bidNumber: item.bfSpecRgstNo,
          amount: item.asignBdgtAmt,
          announcementDate: item.rgstDt,
          deadline: '',
          contractMethod: '',
          // fileList
        });
      }

      // 🔹 사전규격 - 용역 데이터 추가 - type : 1
      if (item.type === 1) {
        const fileList = [];
        for (let i = 1; i <= 5; i++) {
          const fileName = `첨부파일${i}`;
          const fileUrl = item[`specDocFileUrl${i}`];

          if (fileName && fileName.trim() !== "" && fileUrl && fileUrl.trim() !== "") {
            fileList.push({ name: fileName, url: fileUrl });
          }
        }
        results.push({
          no: index + 1,
          category: item.bsnsDivNm,
          bidType: "사전규격",
          title: item.prdctClsfcNoNm,
          organization: item.rlDminsttNm,
          bidNumber: item.bfSpecRgstNo,
          amount: item.asignBdgtAmt,
          announcementDate: item.rgstDt,
          deadline: '',
          contractMethod: '',
          // fileList
        });
      }

      return results;
    });
  }, [bidInfos]);

  // 파일 다운로드 함수
  const handleFileDownload = (url) => {
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  const [columnDefs] = useState([
    {
      headerName: "",
      field: "checkbox",
      width: 50,
      checkboxSelection: true,
      headerCheckboxSelection: true,
      suppressSizeToFit: true, // 셀 크기 자동 조절 방지
    },
    { headerName: "No", field: "no", width: 70 },
    { headerName: "구분", field: "category", width: 100 },
    {
      headerName: "입찰유형", field: "bidType", width: 100,
      cellRenderer: (params) => params.value,  // ✅ 기본 표시 방식 유지
    },
    {
      headerName: "공고명", field: "title", width: 350, filter: "agTextColumnFilter",
      cellRenderer: (params) => {
        if (!params.data?.pageUrl) {
          return <span>{params.value}</span>; // 🔹 기본 텍스트
        }
        return (
          <a href={params.data.pageUrl} target="_blank" rel="noopener noreferrer">
            {params.value}
          </a>
        );
      },
    },
    {
      headerName: "수요기관", field: "organization", width: 120,
    },
    { headerName: "공고번호", field: "bidNumber", width: 150, filter: "agTextColumnFilter" },
    {
      headerName: "기초금액", field: "amount", width: 140,
      cellRenderer: (params) => {
        return (
          <div className={styles.amtCell}>
            <span>{params.valueFormatted}</span>
          </div>
        )
      }
      , sortable: true, filter: "agNumberColumnFilter",
      valueFormatter: (params) => {
        if (params.value == null) return '';
        return Number(params.value).toLocaleString() + "원";
      },
      comparator: (valueA, valueB) => {
        return Number(valueA) - Number(valueB);
      }
    },
    { headerName: "공고일", field: "announcementDate", width: 120, filter: "agDateColumnFilter" },
    { headerName: "마감일", field: "deadline", width: 120, filter: "agDateColumnFilter" },
    { headerName: "계약방법", field: "contractMethod", width: 100, },
    {
      headerName: "첨부파일", field: "fileUrl", width: 200,
      cellRenderer: (params) => {
        if (!params.data.fileList || params.data.fileList.length === 0) return null;

        return (
          <div className={styles.fileContent}>
            <div className={styles.fileUrl} >
              {
                params.data.fileList.map((file, index) => (
                  <div key={index} className={styles.fileUrl__oneDiv}>
                    <button className={styles.fileUrl__oneDiv__btnTag} onClick={(e) => {
                      handleFileDownload(file.url);
                    }}>
                      <img className={styles.fileUrl__oneDiv__imgTag} src='/icons/icon-download.png' alt="" />
                    </button>
                    <a className={styles.fileUrl__oneDiv__aTag} href={file.url} target="_blank" rel="noopener noreferrer" >
                      {file.name}
                    </a>
                  </div>
                ))
              }
            </div>
          </div>
        )
      }
    },
  ]);

  // 첨부파일 행 높이 조절
  const getRowHeight = useCallback((params) => {
    if (params.data?.fileList?.length > 1) {
      return Math.min(30 + params.data.fileList.length * 18, 65); // 기본 높이 + 파일 개수에 따른 가변 높이
    }
    return 30; // 기본 높이
  }, [])

  // 정렬방식
  const defaultColDef = useMemo(() => ({
    filter: true, // ✅ 필터 활성화
    sortable: true, // ✅ 정렬 활성화
    // resizable: true, // ✅ 컬럼 크기 조절 가능
    tooltipComponentFramework: CustomTooltip, // ✅ 모든 컬럼에 자동 적용
    tooltipValueGetter: (params) => params.value,
  }));

  const gridOptions = {
    domLayout: 'autoHeight',
    pagination: true,
    paginationPageSize: 30,
  };
  // 메시지 보내기 클릭시 total 전송 개수 체크(7개 제한)
  const checkTotalRows = () => {
    // 최대 7개까지 메시지 전송 가능
    if (selectedRows.length > 7) {
      toast.info('최대 7개까지 선택할 수 있습니다. 추가 선택을 원하시면 기존 선택을 해제하세요.');
      return;
    }
    onSendState(true);
  }
  // 선택된 행 담기
  const onSelectionChanged = useCallback(() => {
    if (gridRef.current && gridRef.current.api) {
      const newSelectedRows = gridRef.current.api.getSelectedRows();
      setSelectedRows(newSelectedRows);
    }
  }, [setSelectedRows])

  // 📌 엑셀 다운로드 함수
  const exportToExcel = async () => {
    if (!gridRef.current) return;

    const selectedRows = gridRef.current.api.getSelectedRows();
    if (selectedRows.length === 0) {
      toast.error("선택된 데이터가 없습니다. 먼저 행을 선택하세요.");
      return;
    }

    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet("입찰공고");

    worksheet.columns = [
      { header: "No", key: "no", width: 5 },
      { header: "구분", key: "category", width: 10 },
      { header: "입찰유형", key: "bidType", width: 15 },
      { header: "공고명", key: "title", width: 40 },
      { header: "수요기관", key: "organization", width: 20 },
      { header: "공고번호", key: "bidNumber", width: 15 },
      { header: "기초금액", key: "amount", width: 15 },
      { header: "공고일", key: "bidDate", width: 20 },
      { header: "마감일", key: "deadline", width: 20 },
      { header: "계약방법", key: "contractMethod", width: 15 }
    ];

    selectedRows.forEach((row, index) => {
      console.log(row)
      const rowData = worksheet.addRow({
        no: index + 1,
        category: row.category,
        bidType: row.bidType,
        title: row.title,
        organization: row.organization,
        bidNumber: row.bidNumber,
        amount: row.amount ? `${Number(row.amount).toLocaleString()} 원` : "", // ✅ 쉼표 추가 + '원' 붙이기
        bidDate: row.announcementDate,
        deadline: row.deadline,
        contractMethod: row.contractMethod
      });


      if (row.pageUrl) {
        // ✅ 공고명에 하이퍼링크 적용 + 스타일 추가 (pageUrl이 있을 경우만)
        rowData.getCell("title").value = {
          text: row.title,
          hyperlink: row.pageUrl
        };
        rowData.getCell("title").font = {
          color: { argb: "FF0000FF" }, // 파란색 (#0000FF)
          underline: true
        };
      }
      // ✅ 기초금액을 **오른쪽 정렬**
      if (row.amount) {
        rowData.getCell("amount").alignment = { horizontal: "right" };
      }
      // ✅ 공고일 및 마감일 날짜 형식 적용 (MM/DD/YY HH:MM)
      if (row.bidDate) {
        rowData.getCell("bidDate").value = new Date(row.bidDate);
        rowData.getCell("bidDate").numFmt = "MM/DD/YY HH:MM";
      }
      if (row.deadline) {
        rowData.getCell("deadline").value = new Date(row.deadline);
        rowData.getCell("deadline").numFmt = "MM/DD/YY HH:MM";
      }

    });

    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
    saveAs(blob, `입찰공고_${new Date().toISOString().split("T")[0]}.xlsx`);
  };
  return (
    <div className={`ag-theme-alpine ${styles.contents}`}>
      {/* ✅ 필터링을 위한 드롭다운 메뉴 추가 */}
      {/* <div style={{ marginBottom: "10px" }}>
        <label>입찰유형 필터: </label>
        <select onChange={handleFilterChange}>
          <option value="">전체</option>
          {bidTypeOptions.map((type, index) => (
            <option key={index} value={type}>{type}</option>
          ))}
        </select>
      </div> */}
      <div>
        <CommonTooltip text="엑셀로 다운로드">
          <button onClick={exportToExcel} className={styles.contents__exelImg}>
            <img src='/icons/icon-exel.png' alt="" />
          </button>
        </CommonTooltip>
        <CommonTooltip text="메시지 보내기">
          <button onClick={checkTotalRows} className={styles.contents__kakaoImg}>
            <img src='/icons/icon-kakao.png' alt="" />
          </button>
        </CommonTooltip>
      </div>
      {/* 만약 데이터가 없을 때 */}
      {isLoading ?
        <div className={styles.noData}> <Loading /> </div> :
        (rowData.length == 0 ? (
          <div className={styles.noData}>조회 가능한 데이터가 없습니다.</div>
        ) : (
          <AgGridReact
            ref={gridRef}
            columnDefs={columnDefs}
            rowData={rowData || []}
            rowSelection="multiple" // 다중 선택
            rowMultiSelectWithClick={true}
            // onRowSelected={onRowSelected} // 개별 행 선택 이벤트 핸들러
            onSelectionChanged={onSelectionChanged} // 선택 변경 이벤트 핸들러
            enableBrowserTooltips={false} // ✅ 툴팁 활성화
            tooltipShowDelay={0} // ✅ 툴팁 즉시 표시
            gridOptions={gridOptions}
            defaultColDef={defaultColDef}
            getRowHeight={getRowHeight}
            enableCellTextSelection={true}  // ✅ 텍스트 드래그 활성화
            suppressRowClickSelection={false}  // ✅ 클릭 시 행 선택 방지
            paginationPageSizeSelector={[5, 10, 20, 50]} // 선택 가능한 페이지 크기
          />
        ))}
    </div>
  );
};

export default MyTable;