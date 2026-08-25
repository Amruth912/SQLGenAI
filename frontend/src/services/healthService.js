import apiClient from './apiClient';

export const healthService = {
  getHealth: async () => {
    return await apiClient.get('/health');
  },
};
