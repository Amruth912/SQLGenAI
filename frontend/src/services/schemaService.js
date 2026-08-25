import apiClient from './apiClient';

export const schemaService = {
  getSchema: async (schemaName = 'public') => {
    return await apiClient.get('/schema', {
      params: { schema: schemaName }
    });
  },
};
