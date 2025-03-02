import { useState } from 'react';
import styles from './SearchBox.module.scss'
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";


function SearchBox() {
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [proItemInput, setProItemInput] = useState("프로세스제어반");   // 사전규격 세부품목
    const [bidItemInput, setBidItemInput] = useState("프로세스제어반");   // 입찰공고 세부품목
    const [bidRegionInput, setBidRegionInput] = useState("전국");   // 입찰공고 제한지역
    const [bidMethodInput, setBidMethodInput] = useState("일반경쟁");   // 입찰공고 계약방법
    const [searchInput, setSearchInput] = useState("");
    const [proItems, setProItems] = useState([]);
    const [bidItems, setBidItems] = useState([]);
    const [bidRegions, setBidRegions] = useState([]);
    const [bidMethods, setBidMethods] = useState([]);
    const [searchTerms, setSearchTerms] = useState([]);


    // 체크박스 선택/해제
    const toggleCategory = (category, setItems) => {
        setItems((prev) =>
            prev.includes(category)
                ? prev.filter((item) => item !== category) // 체크 해제 시 제거
                : [...prev, category] // 체크 시 추가
        );
    };


    // 중복 체크 후 상태 업데이트 함수
    const addItem = (input, setItems, setInput) => {
        if (input.trim() !== "") {
            setItems(prev => (prev.includes(input) ? prev : [...prev, input]));
            setInput("");
        }
    };

    // 검색어 추가
    const addSearchTerm = () => addItem(searchInput, setSearchTerms, setSearchInput);
    const addBidItem = (category) => toggleCategory(category, setBidItems);
    const addProItem = (category) => toggleCategory(category, setProItems);
    const addBidRegion = (category) => toggleCategory(category, setBidRegions);
    const addBidMethod = (category) => toggleCategory(category, setBidMethods);

    // enter로 검색어 추가
    const handleKeyDown = (e) => {
        if (e.nativeEvent.isComposing) return;
        if (e.key === 'Enter') {

            e.preventDefault(); // 기본 이벤트 방지 (특정 환경에서 중복 실행 방지)
            console.log(e);
            addSearchTerm()
        }
    }

    // 검색어 삭제
    const removeSearchTerm = (text, index) => {
        if (text == 'terms') setSearchTerms(searchTerms.filter((_, i) => i !== index));
        else if (text == 'proItem') setProItems(proItems.filter((_, i) => i !== index));
        else if (text == 'bidItem') setBidItems(bidItems.filter((_, i) => i !== index));
        else if (text == 'bidRegion') setBidRegions(bidRegions.filter((_, i) => i !== index));
        else if (text == 'bidMethod') setBidMethods(bidMethods.filter((_, i) => i !== index));
    };

    return (
        <div className={styles.searchBox}>
            <div className={styles.searchBox__detail}>
                {/* 검색 일자 설정 */}
                <div className={styles.dateBox}>
                    <div className={styles.dateBox__start}>
                        <label className={styles.dateBox__label}>조회 시작일</label>
                        <DatePicker
                            selected={startDate}
                            onChange={setStartDate}
                            dateFormat="yyyy/MM/dd"
                            className="border p-2 rounded w-full"
                            placeholderText="YYYY/MM/DD"
                        />
                    </div>
                    <div className={styles.dateBox__end}>
                        <label className={styles.dateBox__label}>조회 종료일</label>
                        <DatePicker
                            selected={endDate}
                            onChange={setEndDate}
                            dateFormat="yyyy/MM/dd"
                            className="border p-2 rounded w-full"
                            placeholderText="YYYY/MM/DD"
                        />
                    </div>
                </div>
                <div className={styles.wrapDetail}>
                    {/* 테이블생성 */}
                    <table className={styles.table}>
                        <tbody>
                            <tr>
                                <td rowSpan={3} className={styles.table__title}>사전규격 <br />&nbsp;- 일반용역, 기술용역</td>
                                <td className={styles.table__searchBar}>
                                    <div className={styles.searchBar}>
                                        <input type='text' placeholder='검색어를 입력하세요' className={styles.searchBar__input}
                                            value={searchInput}
                                            onChange={(e) => setSearchInput(e.target.value)}
                                            onKeyDown={handleKeyDown} />
                                        <button onClick={addSearchTerm} className={styles.searchBar__btn}>
                                            <img src='src/assets/icons/icon-plus.png' alt="" />
                                        </button>
                                    </div>
                                </td>
                                <td></td>
                            </tr>
                            <tr>
                                <td rowSpan={2} colSpan={2} className={styles.table__searchResult} style={{ backgroundColor: "main.$color-gray-100" }}>
                                    <div className={styles.resultBox}>
                                        <div className={styles.searchBar__results}>
                                            {/* 검색어에 추가 결과 표시부분 */}
                                            {searchTerms.map((term, index) => (
                                                <div key={index} className={styles.searchBar__results__tag}>
                                                    {term}
                                                    <button className={styles.searchBar__search__btn}
                                                        onClick={() => removeSearchTerm('terms', index)}>
                                                        <img src='src/assets/icons/icon-cross.png' alt="" />
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr></tr>
                            <tr>
                                <td rowSpan={3} className={styles.table__title} >사전규격 - 물품</td>
                                <td className={styles.table__wrapMid} colSpan={2}>
                                    {/* 세부품목선택 */}
                                    <div className={styles.table__title__mid}>세부 품목</div>
                                </td>
                            </tr>
                            <tr>
                                <td rowSpan={2} colSpan={2} className={styles.table__checkResult}>
                                    <div className={styles.resultBox}>
                                        <div className={styles.checkBox}>
                                            {["프로세스제어반", "계장제어장치", "유량계"].map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.checkBox__btn} ${bidItems.includes(category) ? styles.checkBox__selected : styles.checkBox__btn}`}
                                                    onClick={() => addBidItem(category)}
                                                >
                                                    {category}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr></tr>
                            <tr>
                                <td rowSpan={4} className={styles.table__title}>입찰공고 - 물품</td>
                                <td className={styles.table__wrapMid}>
                                    {/* 세부품목선택 */}
                                    <div className={styles.table__title__mid}>세부 품목</div>
                                </td>
                                <td className={styles.table__wrapMid}>
                                    <div className={styles.table__title__mid}>제한지역</div>
                                </td>
                            </tr>
                            <tr>
                                <td rowSpan={3}>
                                    <div className={styles.resultBox}>
                                        <div className={styles.checkBox}>
                                            {["프로세스제어반", "계장제어장치", "유량계"].map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.checkBox__btn} ${proItems.includes(category) ? styles.checkBox__selected : styles.checkBox__btn}`}
                                                    onClick={() => addProItem(category)}
                                                >
                                                    {category}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <div className={styles.resultMid}>
                                        <div className={styles.resultMid__checkBox}>
                                            {["전국(제한없음)", "인천광역시"].map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.resultMid__checkBox__btn} ${bidRegions.includes(category) ? styles.resultMid__checkBox__selected : styles.resultMid__checkBox__btn}`}
                                                    onClick={() => addBidRegion(category)}
                                                >
                                                    {category}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td className={styles.table__wrapMid}>
                                    <div className={styles.table__title__mid}>계약방법</div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div className={styles.resultMid}>
                                        <div className={styles.resultMid__checkBox}>
                                            {["일반경쟁", "지역경쟁"].map((category) => (
                                                <button
                                                    key={category}
                                                    className={`${styles.resultMid__checkBox__btn} ${bidMethods.includes(category) ? styles.resultMid__checkBox__selected : styles.resultMid__checkBox__btn}`}
                                                    onClick={() => addBidMethod(category)}
                                                >
                                                    {category}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div className={styles.searchBox__btn}>
                <div className={styles.searchBox__wrapBtn}>
                    {/* 버튼 생성 */}
                    <button className={styles.searchBox__btn__edit}>기본 검색 <br />조건으로 설정</button>
                    <button className={styles.searchBox__btn__default}>기본 검색 조건 <br />적용</button>
                    <button className={styles.searchBox__btn__search}>검색</button>
                </div>
            </div>
        </div>
    )
}

export default SearchBox