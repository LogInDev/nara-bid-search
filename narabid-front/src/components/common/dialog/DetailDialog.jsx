import { AgGridReact } from 'ag-grid-react'
import styles from './DetailDialog.module.scss'
import { useEffect, useMemo, useState } from 'react'
import toast, { toastConfig } from 'react-simple-toasts'
import 'react-simple-toasts/dist/theme/dark.css'

toastConfig({ theme: 'dark' })

function DetailDialog({ handleDialog }) {
    const [bookmark, setBookmark] = useState(false)
    // 다이얼로그 끄기
    const closeDialog = () => {
        handleDialog(false)
    }

    // 북마크 추가 이벤트
    const addBookmark = (selected) => {
        setBookmark(true)

        const getLocalStorage = JSON.parse(localStorage.getItem("bookmark"))
        // 1. 로컬스토리지에 bookmark이라는 데이터가 없을 경우
        if (!getLocalStorage || getLocalStorage == null) {
            localStorage.setItem('bookmark', JSON.stringify([selected]))
            toast("해당 이미지를 북마크에 저장하였습니다. 👍")
        } else {
            // 2. 해당 이미지가 이미 로컬스토리지 bookmark라는 데이터에 저장되어 있을 경우
            if (getLocalStorage.findIndex((item) => item.id == selected.id) > -1) {
                toast("해당 이미지는 이미 북마크에 추가된 상태입니다. ❌")
            } else {
                // 3. 해당 이미지가 로컬스토리지 bookmark라는 데이터에 저장되어 있지 않을 경우 + bookmark라는 데이터에 이미 어떤 값이 담겨 있는 경우
                const res = [...getLocalStorage]
                res.push(selected)
                localStorage.setItem('bookmark', JSON.stringify(res))
                toast("해당 이미지를 북마크에 저장하였습니다. 👍")
            }
        }
    }



    const rowData = useMemo(() => {
        return [
            { no: 1, category: '1', bidType: '물품' },
            { no: 2, category: '2', bidType: '용역' },
            { no: 3, category: '3', bidType: '공사' },
            { no: 4, category: '4', bidType: '기타' },
        ]
    }, [])
    const [columnDefs] = useState([
        { headerName: "No", field: "no", sortable: true, filter: true, width: 70 },
        { headerName: "세부 품목 번호", field: "category", sortable: true, filter: true, width: 100 },
        {
            headerName: "세부 품목명", field: "bidType", width: 100,
        },
    ]);
    // 정렬방식
    const defaultColDef = useMemo(() => ({
        filter: true, // ✅ 필터 활성화
        sortable: true, // ✅ 정렬 활성화
    }));
    const gridOptions = {
        domLayout: 'autoHeight',
    };

    // useEffect(() => {
    //     const getLocalStorage = JSON.parse(localStorage.getItem('bookmark'))

    //     if (getLocalStorage && getLocalStorage.findIndex((item) => item.id == data.id) > -1) {
    //         setBookmark(true)
    //     } else if (!getLocalStorage) return

    //     // ESC 키를 눌렀을 때, 다이얼로그 창 닫기
    //     const escKeyDownCloseDialog = (event) => {
    //         if (event.key == 'Escape') {
    //             closeDialog()
    //         }
    //     }
    //     // 위에 만들어놓은 escKeyDownCloseDialog를 키다운 했을 때, 이벤트로 등록 및 해지
    //     window.addEventListener('keydown', escKeyDownCloseDialog)
    //     return () => document.removeEventListener('keydown', escKeyDownCloseDialog)
    // }, [])

    return (
        <div className={styles.container}>
            <div className={styles.container__dialog}>
                <div className={styles.container__dialog__header}>
                    <div className={styles.close}>
                        <span className={styles.close__authorName}>세부 품목 추가</span>
                    </div>
                    <div className={styles.bookmark}>
                        <button className={styles.close__button} onClick={closeDialog}>
                            {/* 구글 아이콘을 사용 */}
                            <span className='material-symbols-outlined' style={{ fontSize: 28 + 'px' }}>
                                close
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__body}>
                    <div className={styles.searchBar}>
                        <input type='text' placeholder='세부 품명을 입력하세요' className={styles.searchBar__input}
                        />
                        <button className={styles.searchBar__btn}>
                            <span class="material-symbols-outlined" style={{ fontSize: 28 + 'px' }}>
                                search
                            </span>
                        </button>
                    </div>
                </div>
                <div className={styles.container__dialog__footer}>
                    <AgGridReact
                        columnDefs={columnDefs}
                        rowData={rowData}
                        rowSelection='single'
                        enableCellTextSelection={true}
                    />
                </div>
            </div>
        </div>
    )
}

export default DetailDialog