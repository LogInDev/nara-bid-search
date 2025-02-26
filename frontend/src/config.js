// export const API_BASE_URL = import.meta.env.API_BASE_URL || "http://localhost:8080";
const API_BASE_URL = window.APP_CONFIG?.API_BASE_URL || "http://nivusds.iptime.org:9291";
export { API_BASE_URL };