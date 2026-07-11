import axiosInstance from './axiosInstance';

export const getAllUsers = () => axiosInstance.get('/users').then((res) => res.data);
export const assignRole = (id, role) =>
  axiosInstance.put(`/users/${id}/role`, { role }).then((res) => res.data);