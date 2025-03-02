import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import axios from "axios";
import { API_BASE_URL } from "../config";
"../styles/ProcurementTable.css";


const ProcurementTable = () => {
  const [selectedCategories, setSelectedCategories] = useState([]); // 검색 카테고리 선택 - 체크박스
  const [startDate, setStartDate] = useState(null); // 조회 시작일
  const [endDate, setEndDate] = useState(null); // 조회 종료일
  const [itemInput, setItemInput] = useState("");   // 세부 품목 입력
  const [searchInput, setSearchInput] = useState("");   // 검색어 입력
  const [items, setItems] = useState([]);   // 입력된 세부 품목 목록
  const [searchTerms, setSearchTerms] = useState([]);   // 입력된 검색어 목록
  const [methodInput, setMethodInput] = useState([]);   // 계약 방법 선택
  const [regionInput, setRegionInput] = useState([]);   // 제한 지역 선택
  const [contractMethods, setContractMethods] = useState([]);   // 선택된 계약 방법 목록
  const [restrictRegions, setRestrictRegions] = useState([]);   // 선제한 지역 목록
  const [methodList, setMethodList] = useState([]);   // API로부터 받아온 계약 방법 목록
  const [regionList, setRegionList] = useState([]);   // API로부터 받아온 제한 지역 목록

  // 계약 방법 목록 가져오기    
    useEffect(() => {
        fetch(`${API_BASE_URL}/api/contract-methods`) // API로부터 계약 방법 목록 가져오기
        .then((response) => response.json())
        .then((data) => setMethodList(data))
        .catch((error) => console.error("Error fetching contract methods:", error));
    }, []);

  

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

  // 제한지역 추가
  const addRestrictResion = () => {
    if (regionInput.trim() !== "") {
      setRestrictRegions([...restrictRegions, regionInput]);
      setRegionInput("");
    }
  };

  // 계약방법 추가
  const addContractMethod = () => {
    if (methodInput.trim() !== "") {
      setContractMethods([...contractMethods, methodInput]);
      setMethodInput("");
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

  // 제한지역 삭제
  const removeRistrictRegion = (index) => {
    setRestrictRegions(restrictRegions.filter((_, i) => i !== index));
  };

  // 계약방법 삭제
  const removeContractMethod = (index) => {
    setContractMethods(contractMethods.filter((_, i) => i !== index));
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
      <div className="space-y-6">
        {/* 체크박스 */}
        <div className="">
          {["사전규격 - 물품", "사전규격 - 일반, 기술용역", "입찰공고 - 물품"].map(
            (category) => (
              <label key={category} className="">
                <input
                  type="checkbox"
                  checked={selectedCategories.includes(category)}
                  onChange={() => toggleCategory(category)}
                />
                <span>{category}</span>
              </label>
            )
          )}
        </div>

        {/* 날짜 입력 & 제한지역 선택 */}
        <div className="date-filters">
          <div >
            <label className="">조회 시작일</label>
            <DatePicker
              selected={startDate}
              onChange={setStartDate}
              dateFormat="yyyy/MM/dd"
              className="border p-2 rounded w-full"
              placeholderText="YYYY/MM/DD"
            />
          </div>
          <div>
            <label >조회 종료일</label>
            <DatePicker
              selected={endDate}
              onChange={setEndDate}
              dateFormat="yyyy/MM/dd"
              className="border p-2 rounded w-full"
              placeholderText="YYYY/MM/DD"
            />
          </div>
        </div>

        {/* 세부 품목 & 검색어 입력 & 계약방법 선택 */}
        <div className="search-filters">
          {/* 세부 품목 */}
            <div className="search-result">
            <div className="search-filters">
                    <label className="">세부 품목</label>
                    <div className="">
                    <input
                        type="text"
                        value={itemInput}
                        onChange={(e) => setItemInput(e.target.value)}
                        className=""
                        placeholder="세부 품목 입력"
                    />
                    <button onClick={addItem} className="">
                        추가
                    </button>
                    </div>
                </div>
                <div>
                    <div className="">
                    {items.map((item, index) => (
                        <span key={index} className="">
                        {item}
                        <button
                            onClick={() => removeItem(index)}
                            className=""
                        >
                        </button>
                        </span>
                    ))}
                    </div>
                </div>
            </div>

          {/* 검색어 */}
          <div className="search-filters">
            <label className="">검색어</label>
            <div className="">
              <input
                type="text"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                className=""
                placeholder="검색어 입력"
              />
              <button onClick={addSearchTerm} className="">
                추가
              </button>
            </div>
            <div className="">
              {searchTerms.map((term, index) => (
                <span key={index} className="">
                  {term}
                  <button
                    onClick={() => removeSearchTerm(index)}
                    className=""
                  >
                  </button>
                </span>
              ))}
            </div>
          </div>

          {/* 계약방법 */}
          <div className="search-filters">
            <label className="">제한지역</label>
            <div className="">
              <select
                value={methodInput}
                onChange={(e) => setMethodInput(e.target.value)}
                className=""
              />
                <option value="">제한지역 선택</option>
                {methodList.map((method) => (
                    <option key={method} value={method}>{method}</option>
                ))}
              <button onClick={addContractMethod} className="">
                추가
              </button>
            </div>
            <div className="">
              {searchTerms.map((term, index) => (
                <span key={index} className="">
                  {term}
                  <button
                    onClick={() => removeContractMethod(index)}
                    className=""
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
        <div className="">
          <button style={{height: "150px", width: "100px"}}
 className=""
 onClick={handleSearch}>
            검색
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProcurementTable;

