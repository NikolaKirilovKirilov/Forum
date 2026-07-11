import axiosInstance from './axiosInstance';

export const createReply = (topicId, data) =>
  axiosInstance.post(`/topics/${topicId}/replies`, data).then((res) => res.data);
export const updateReply = (id, data) =>
  axiosInstance.put(`/replies/${id}`, data).then((res) => res.data);