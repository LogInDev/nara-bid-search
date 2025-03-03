import React from 'react'
import MyTable from './components/Mytable'
import SearchBox from './components/SearchBox'
import { useBidInfo } from '@/store/apiContext'

function MainPage() {
    const { bidInfos } = useBidInfo();

    return (
        <div>
            <h1>나라장터 검색 시스템</h1>
            <br />
            <SearchBox />
            <br />
            <MyTable />
        </div>
    )
}

export default MainPage