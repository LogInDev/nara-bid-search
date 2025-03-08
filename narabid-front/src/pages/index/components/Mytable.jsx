import styles from './Mytable.module.scss'
import React, { useState, useRef, useCallback, useMemo } from "react";
import { AgGridReact } from "ag-grid-react";
import { ModuleRegistry } from "ag-grid-community";
import { ClientSideRowModelModule, CsvExportModule } from "ag-grid-community"; // ✅ SetFilterModule 추가

import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { useBidInfo } from '@/store/apiContext';
import CustomTooltip from '@/components/common/tooltip/CustomTooltip';
import Loading from './Loading';

ModuleRegistry.registerModules([ClientSideRowModelModule, CsvExportModule]);

const MyTable = () => {
  const gridRef = useRef(null);
  const { bidInfos, isLoading } = useBidInfo();

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
    { headerName: "No", field: "no", sortable: true, filter: true, width: 70 },
    { headerName: "구분", field: "category", sortable: true, filter: true, width: 100 },
    {
      headerName: "입찰유형", field: "bidType", width: 100,
      cellRenderer: (params) => params.value,  // ✅ 기본 표시 방식 유지
    },
    {
      headerName: "공고명", field: "title", sortable: true, width: 350, filter: "agTextColumnFilter",
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
      headerName: "수요기관", field: "organization", width: 120, sortable: true, filter: true,
    },
    { headerName: "공고번호", field: "bidNumber", width: 150, sortable: true, filter: "agTextColumnFilter" },
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
    { headerName: "공고일", field: "announcementDate", width: 120, sortable: true, filter: "agDateColumnFilter" },
    { headerName: "마감일", field: "deadline", width: 120, sortable: true, filter: "agDateColumnFilter" },
    { headerName: "계약방법", field: "contractMethod", width: 100, sortable: true, filter: true },
    {
      headerName: "첨부파일", field: "fileUrl", sortable: true, width: 200,
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
    // paginationAutoPageSize: false, // ✅ 필요에 따라 자동 페이지 크기 사용 여부 설정
  };

  // 📌 엑셀 다운로드 함수
  const exportToExcel = useCallback(() => {
    if (gridRef.current) {
      // gridRef.current.api.exportDataAsCsv();
      gridRef.current.api.exportDataAsCsv({
        fileName: `입찰공고_${new Date().toISOString().split("T")[0]}.csv`, // ✅ 파일명: "YYYY-MM-DD_입찰공고.csv"
        processCellCallback: (params) => {
          if (params.column.getColId() === "fileUrl") {
            return null; // ✅ "fileUrl" 필드 값 제외
          }
          return params.value;
        },
        processHeaderCallback: (params) => {
          if (params.column.getColId() === "fileUrl") {
            return null; // ✅ "fileUrl" 헤더도 제외
          }
          return params.column.getColDef().headerName || params.column.getColId();
        },
      });
    }
  }, []);

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
      <button onClick={exportToExcel} style={{ marginBottom: "10px" }}>엑셀 다운로드</button>
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
            enableBrowserTooltips={false} // ✅ 툴팁 활성화
            tooltipShowDelay={0} // ✅ 툴팁 즉시 표시
            gridOptions={gridOptions}
            defaultColDef={defaultColDef}
            getRowHeight={getRowHeight}
            enableCellTextSelection={true}  // ✅ 텍스트 드래그 활성화
            suppressRowClickSelection={true}  // ✅ 클릭 시 행 선택 방지
            paginationPageSizeSelector={[5, 10, 20, 50]} // 선택 가능한 페이지 크기
          />
        ))}
    </div>
  );
};

export default MyTable;