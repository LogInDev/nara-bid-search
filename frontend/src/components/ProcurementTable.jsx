import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import axios from "axios";
import { API_BASE_URL } from "../config";


const ProcurementTable = () => {
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [itemInput, setItemInput] = useState("");
  const [searchInput, setSearchInput] = useState("");
  const [items, setItems] = useState([]);
  const [searchTerms, setSearchTerms] = useState([]);

  // 체크박스 선택/해제
  const toggleCategory = (category) => {
    setSelectedCategories((prev) =>
      prev.includes(category)
        ? prev.filter((c) => c !== category)
        : [...prev, category]
    );
  };

  // 세부 품목 추가
  const addItem = () => {
    if (itemInput.trim() !== "") {
      setItems([...items, itemInput]);
      setItemInput("");
    }
  };

  // 검색어 추가
  const addSearchTerm = () => {
    if (searchInput.trim() !== "") {
      setSearchTerms([...searchTerms, searchInput]);
      setSearchInput("");
    }
  };

  // 세부 품목 삭제
  const removeItem = (index) => {
    setItems(items.filter((_, i) => i !== index));
  };

  // 검색어 삭제
  const removeSearchTerm = (index) => {
    setSearchTerms(searchTerms.filter((_, i) => i !== index));
  };


  const handleSearch = async () => {
    const requestData = {
        categories: selectedCategories,
        startDate: startDate? startDate.toISOString().split("T")[0] : null,
        endDate: endDate? endDate.toISOString().split("T")[0] : null,
        items: items,
        searchTerms: searchTerms,
      };

        try {
            const response = await axios.post(`${API_BASE_URL}/api/bids/search`, requestData);
            console.log(response.data);
        }
        catch (error) {
            console.error("Error searching bids:", error);
        }
    }

  return (
    <div className="search-outline">
      <div className="p-4 border rounded-lg shadow-md w-full max-w-4xl mx-auto bg-white space-y-6">
        {/* 체크박스 */}
        <div className="grid grid-cols-3 gap-4">
          {["사전규격 - 물품", "사전규격 - 일반, 기술용역", "입찰공고 - 물품"].map(
            (category) => (
              <label key={category} className="flex items-center space-x-2">
                <input
                  type="checkbox"
                  checked={selectedCategories.includes(category)}
                  onChange={() => toggleCategory(category)}
                />
                <span style={{marginRight: "50px"}}>{category}</span>
              </label>
            )
          )}
        </div>

        {/* 날짜 입력 */}
        <div className="date-filters grid grid-cols-2 gap-4">
          <div style={{marginRight: "71px"}}>
            <label className="block text-sm font-medium">조회 시작일</label>
            <DatePicker
              selected={startDate}
              onChange={setStartDate}
              dateFormat="yyyy/MM/dd"
              className="border p-2 rounded w-full"
              placeholderText="YYYY/MM/DD"
            />
          </div>
          <div>
            <label className="block text-sm font-medium">조회 종료일</label>
            <DatePicker
              selected={endDate}
              onChange={setEndDate}
              dateFormat="yyyy/MM/dd"
              className="border p-2 rounded w-full"
              placeholderText="YYYY/MM/DD"
            />
          </div>
        </div>

        {/* 세부 품목 & 검색어 입력 */}
        <div className="search-filters grid grid-cols-2 gap-4">
          {/* 세부 품목 */}
            <div className="search-result">
            <div className="search-filters">
                    <label className="block text-sm font-medium">세부 품목</label>
                    <div className="flex gap-2">
                    <input
                        type="text"
                        value={itemInput}
                        onChange={(e) => setItemInput(e.target.value)}
                        className="border p-2 rounded w-full"
                        placeholder="세부 품목 입력"
                    />
                    <button onClick={addItem} className="bg-blue-500 text-white px-4 py-2 rounded">
                        추가
                    </button>
                    </div>
                </div>
                <div>
                    <div className="mt-2 flex flex-wrap gap-2">
                    {items.map((item, index) => (
                        <span key={index} className="bg-gray-200 p-2 rounded flex items-center">
                        {item}
                        <button
                            onClick={() => removeItem(index)}
                            className="ml-2 text-red-500 font-bold"
                        >
                            ×
                        </button>
                        </span>
                    ))}
                    </div>
                </div>
            </div>

          {/* 검색어 */}
          <div className="search-filters">
            <label className="block text-sm font-medium">검색어</label>
            <div className="flex gap-2">
              <input
                type="text"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                className="border p-2 rounded w-full"
                placeholder="검색어 입력"
              />
              <button onClick={addSearchTerm} className="bg-blue-500 text-white px-4 py-2 rounded">
                추가
              </button>
            </div>
            <div className="mt-2 flex flex-wrap gap-2">
              {searchTerms.map((term, index) => (
                <span key={index} className="bg-gray-200 p-2 rounded flex items-center">
                  {term}
                  <button
                    onClick={() => removeSearchTerm(index)}
                    className="ml-2 text-red-500 font-bold"
                  >
                  </button>
                </span>
              ))}
            </div>
          </div>
        </div>
</div>
<div>
        {/* 검색 버튼 */}
        <div className="h-24 flex justify-center items-center">
          <button style={{height: "150px", width: "100px"}}
 className="bg-blue-600 text-white text-lg px-8 py-4 rounded-lg shadow-md"
 onClick={handleSearch}>
            검색
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProcurementTable;

