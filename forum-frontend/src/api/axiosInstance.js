//import axios from 'axios';
//
//// Point this at your Spring Boot backend
//const api = axios.create({
//  baseURL: 'http://localhost:8080',
//});
//
//// Automatically attach the JWT token (if present) to every request
//api.interceptors.request.use((config) => {
//  const token = localStorage.getItem('token');
//  if (token) {
//    config.headers.Authorization = `Bearer ${token}`;
//  }
//  return config;
//});
//
//// Optional: handle expired/invalid tokens globally
//api.interceptors.response.use(
//  (response) => response,
//  (error) => {
//    if (error.response && error.response.status === 401) {
//      localStorage.removeItem('token');
//      // Optionally redirect to login here, e.g. window.location.href = '/login';
//    }
//    return Promise.reject(error);
//  }
//);
//
//export default api;

import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;