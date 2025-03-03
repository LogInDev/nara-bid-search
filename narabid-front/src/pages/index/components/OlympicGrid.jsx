import React, { useEffect, useRef, useState } from "react";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";

const OlympicGrid = () => {
    const gridRef = useRef(null);
    const [rowData, setRowData] = useState([]);

    const columnDefs = [
        { field: "athlete", minWidth: 170 },
        { field: "age" },
        { field: "country" },
        { field: "year" },
        { field: "date" },
        { field: "sport" },
        { field: "gold" },
        { field: "silver" },
        { field: "bronze" },
        { field: "total" },
    ];

    useEffect(() => {
        fetch("https://www.ag-grid.com/example-assets/olympic-winners.json")
            .then((response) => response.json())
            .then((data) => setRowData(data));
    }, []);

    return (
        <div className="ag-theme-alpine" style={{ height: "100vh", width: "100%" }}>
            <AgGridReact
                ref={gridRef}
                columnDefs={columnDefs}
                rowData={rowData}
                pagination={true}         // ✅ 페이지네이션 활성화
                paginationPageSize={30}   // ✅ 페이지당 30개씩
                defaultColDef={{
                    editable: true,
                    filter: true,
                    flex: 1,
                    minWidth: 100
                }}
                rowSelection="multiple"
            />
        </div>
    );
};

export default OlympicGrid;
