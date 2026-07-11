import axiosInstance from './axiosInstance';

export const getAllTopics = () => axiosInstance.get('/topics').then((res) => res.data);
export const getTopic = (id, page = 0) =>
  axiosInstance.get(`/topics/${id}`, { params: { page } }).then((res) => res.data);
export const createTopic = (data) => axiosInstance.post('/topics', data).then((res) => res.data);
export const updateTopic = (id, data) => axiosInstance.put(`/topics/${id}`, data).then((res) => res.data);