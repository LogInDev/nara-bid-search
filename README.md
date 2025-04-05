# 📘 나라장터 입찰정보 통합검색 시스템

> 조달청 OpenAPI를 활용한 입찰정보 통합 검색 시스템 (Excel 다운로드 & 카카오톡 공유 지원)

## 🔍 개요 (Overview)

이 프로젝트는 조달청에서 제공하는 사전규격/입찰공고 OpenAPI를 기반으로,
- 세부품목/키워드 기반 입찰정보 통합 검색
- 데이터 필터링 및 테이블 표시
- 엑셀 다운로드
- 카카오톡 공유 기능

등을 지원하는 검색 포털입니다.

## ✨ 주요 기능 (Features)

- ✅ 입찰정보 통합 검색 (사전규격 + 입찰공고)
- ✅ 카테고리 필터 (품목, 지역, 계약방법 등)
- ✅ 조건 저장/불러오기
- ✅ 결과 엑셀 내보내기 (.xlsx)
- ✅ 카카오톡 공유 기능

## 📦 사용 기술 스택 (Tech Stack)

### Frontend
- React 18
- Vite
- ag-Grid (테이블 UI)
- SCSS Modules
- react-datepicker
- react-simple-toasts

### Backend
- Spring Boot (예: API Proxy 용)
- JPA
- PostgreSQL (옵션: 저장 필요 시)

### 기타 연동
- 조달청 OpenAPI (사전규격, 입찰공고)
- Kakao JavaScript SDK (공유 기능)

## 📂 사용자 메뉴얼

👉 [📄 사용자 메뉴얼 보러가기](https://github.com/LogInDev/nara-bid-search/blob/main/%EC%9E%85%EC%B0%B0%EC%A0%95%EB%B3%B4%20%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%20%EB%A9%94%EB%89%B4%EC%96%BC.pdf)

## [⚙️ 설치 및 실행 방법](https://github.com/LogInDev/nara-bid-search/blob/main/narabid-front/README.md)

## 🚀 배포 환경

- OS: Windows
- Web Server: Nginx
- 배포 방법: NSSM + React 빌드 결과물 제공
