import React, { useMemo } from "react";
import { useTable, useSortBy, useFilters } from "react-table";

const BidTable = ({ data }) => {
  const columns = useMemo(
    () => [
      { Header: "No", accessor: "no", disableSortBy: true, disableFilters: true }, // No 열을 정렬/필터에서 제외
      { Header: "구분", accessor: "category" },
      { Header: "공고명", accessor: "title" },
      { Header: "수요기관", accessor: "institution" },
      { Header: "공고번호", accessor: "bidNumber" },
      { Header: "기초금액", accessor: "amount" },
      { Header: "공고일", accessor: "announcementDate" },
      { Header: "마감일", accessor: "deadline" },
      { Header: "데이터 등록일시", accessor: "createdAt" },
    ],
    []
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable(
    { columns, data },
    useFilters,
    useSortBy
  );

  return (
    <table {...getTableProps()} border="1" style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        {headerGroups.map(headerGroup => (
          <tr {...headerGroup.getHeaderGroupProps()}>
            {headerGroup.headers.map(column => (
              <th {...column.getHeaderProps(column.getSortByToggleProps())}>
                {column.render("Header")}
                {column.isSorted ? (column.isSortedDesc ? " 🔽" : " 🔼") : ""}
              </th>
            ))}
          </tr>
        ))}
      </thead>
      <tbody {...getTableBodyProps()}>
        {rows.map((row, index) => {  // 📌 정렬 및 필터링 후의 데이터 순서대로 'No' 재배치
          prepareRow(row);
          return (
            <tr {...row.getRowProps()}>
              {row.cells.map((cell, cellIndex) => (
                <td {...cell.getCellProps()}>
                  {cellIndex === 0 ? index + 1 : cell.render("Cell")} {/* No 열은 index + 1 로 고정 */}
                </td>
              ))}
            </tr>
          );
        })}
      </tbody>
    </table>
  );
};

export default BidTable;
