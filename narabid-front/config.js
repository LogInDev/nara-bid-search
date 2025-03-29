const API_BASE_URL = window.APP_CONFIG?.API_BASE_URL || "http://nivusds.iptime.org:9291";
// const API_BASE_URL = window.APP_CONFIG?.API_BASE_URL || "http://localhost:9291";
// const API_FRONT_BASE_URL = "http://nivusds.iptime.org:9290";
const API_FRONT_BASE_URL = window.APP_CONFIG?.API_FRONT_BASE_URL || "http://localhost:5173";
const KAKAO_CLIENT_ID = "73b6020b74d6524073a6b7b8f7dce121";
const KAKAO_JS_KEY = "4da47948fb547638c3df026742185921";
export { API_BASE_URL, API_FRONT_BASE_URL, KAKAO_CLIENT_ID, KAKAO_JS_KEY };