import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom'

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
// 페이지 컴포넌트
import MainPage from '@pages/index/index'

function App() {
  return (
    <BrowserRouter>
      <ToastContainer position="top-center" autoClose={1500}          // ✅ 1.5초 후 자동 종료 (원하는 시간으로 조정 가능)
        hideProgressBar           // ✅ 진행 바 숨김
        newestOnTop               // ✅ 최신 메시지를 상단에 표시
        closeOnClick
        pauseOnFocusLoss={false}  // ✅ 다른 창으로 이동해도 정지하지 않음
        draggable
        pauseOnHover={false}      // ✅ 마우스 호버 시에도 일시정지 안 함
        theme="colored"             // 테마: 'light', 'dark', 'colored' 중 택 1
      />
      <Routes>
        <Route index path="/" element={<MainPage />}></Route>
        <Route path='/search/:id' element={<MainPage />}></Route>
      </Routes>
    </BrowserRouter>
  )

}

export default App