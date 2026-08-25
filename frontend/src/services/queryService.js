import apiClient from './apiClient';

export const queryService = {
  /**
   * Send a natural-language question to the backend.
   * Returns { generatedSql, columns, rows, rowCount, executionTimeMs, error }
   */
  async executeQuery(question, schemaName = null) {
    return apiClient.post('/query', { question, schemaName });
  },
};
