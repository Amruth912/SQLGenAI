import apiClient from './apiClient';

export const queryService = {
  /**
   * Send a natural-language question or execution confirmation to the backend.
   * Returns QueryResponse object.
   */
  async executeQuery(question, schemaName = null, confirmed = false, sqlToExecute = null) {
    return apiClient.post('/query', { question, schemaName, confirmed, sqlToExecute });
  },
};
