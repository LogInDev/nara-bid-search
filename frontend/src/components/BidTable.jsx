import React, { useEffect, useState } from "react";
import { useTable, useFilters, useSortBy } from "react-table";

const BidTable = () => {
  const [bids, setBids] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/bids/all") // Spring Boot API 호출
      .then((response) => response.json())
      .then((data) => setBids(data))
      .catch((error) => console.error("Error fetching bids:", error));
  }, []);

  // 테이블 컬럼 정의
  const columns = React.useMemo(
    () => [
      { Header: "No", accessor: "no", disableFilters: true, disableSortBy: true }, // 정렬/필터 제외
      { Header: "구분", accessor: "category" },
      { Header: "사전규격", accessor: "bidType" },
      { Header: "공고명", accessor: "title" },
      { Header: "수요기관", accessor: "institution" },
      { Header: "공고번호 (사전규격등록번호)", accessor: "bidNumber" },
      { Header: "기초금액 (배정예산액)", accessor: "estimatedAmount",
        Cell: ({ value }) => value.toLocaleString() + " 원" },
      { Header: "공고일 (공개일시)", accessor: "announcementDate",
        Cell: ({ value }) => new Date(value).toLocaleString() },
      { Header: "마감일일 (의견등록마감)", accessor: "deadline",
        Cell: ({ value }) => new Date(value).toLocaleString() },
      { Header: "계약방법", accessor: "contractMethod" }
    ],
    []
  );

  // No 컬럼을 추가하여 데이터 가공
  const data = React.useMemo(
    () => bids.map((bid, index) => ({ ...bid, no: index + 1 })),
    [bids]
  );

  // react-table 훅 사용
  const {
    getTableProps,
    getTableBodyProps,
    rows,
    prepareRow,
  } = useTable({ columns, data }, useFilters, useSortBy);

  return (
    <div>
      <h2>입찰 정보</h2>
      <table {...getTableProps()} border="1">
        <thead>
          {/* {headerGroups.map(headerGroup => (
            <tr {...headerGroup.getHeaderGroupProps()}>
              {headerGroup.headers.map(column => (
                <th {...column.getHeaderProps(column.getSortByToggleProps())}>
                  {column.render("Header")}
                  {column.canSort && (
                    <span>{column.isSorted ? (column.isSortedDesc ? " 🔽" : " 🔼") : ""}</span>
                  )}
                </th>
              ))}
            </tr>
          ))} */}
          <tr>
            <th>No</th>
            <th colSpan={2}>구분</th>
            <th>공고명</th>
            <th>수요기관</th>
            <th>공고번호<br/>(사전규격등록번호)</th>
            <th>기초금액<br/>(배정예산액)</th>
            <th>공고일<br/>(공개일시)</th>
            <th>마감일일<br/>(의견등록마감)</th>
            <th>계약방법</th>
          </tr>
        </thead>
        <tbody {...getTableBodyProps()}>
          {rows.map(row => {
            prepareRow(row);
            return (
              <tr {...row.getRowProps()}>
                {row.cells.map(cell => (
                  <td {...cell.getCellProps()}>{cell.render("Cell")}</td>
                ))}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default BidTable;
