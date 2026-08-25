import React, { useState, useRef } from 'react';
import { Send, Loader2, AlertTriangle, Code, Table2 } from 'lucide-react';
import { queryService } from '../../services/queryService';

const EXAMPLE_QUESTIONS = [
  'Show me the top 5 employees with the highest salary',
  'How many employees are in each department?',
  'List all projects and their status',
  'Which employees have been with the company for over 5 years?',
];

export default function QueryPanel() {
  const [question, setQuestion] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const textareaRef = useRef(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!question.trim()) return;

    setLoading(true);
    setResult(null);
    setError(null);

    try {
      const data = await queryService.executeQuery(question.trim());
      setResult(data);
    } catch (err) {
      setError(err.message || 'An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  const useExample = (q) => {
    setQuestion(q);
    setResult(null);
    setError(null);
    textareaRef.current?.focus();
  };

  return (
    <div className="space-y-6">
      {/* Input Form */}
      <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-6">
        <h2 className="text-base font-semibold text-white mb-4 flex items-center gap-2">
          <Send className="w-4 h-4 text-indigo-400" />
          Ask a Question
        </h2>

        <form onSubmit={handleSubmit} className="space-y-3">
          <textarea
            ref={textareaRef}
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) handleSubmit(e);
            }}
            rows={3}
            placeholder="e.g. Show me the top 5 employees with the highest salary…"
            className="w-full bg-slate-950/80 border border-slate-700 text-slate-100 placeholder-slate-500
              rounded-lg px-4 py-3 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500/50
              focus:border-indigo-500/50 transition-colors"
          />
          <div className="flex items-center justify-between gap-3">
            <span className="text-xs text-slate-500">Ctrl + Enter to submit</span>
            <button
              type="submit"
              disabled={loading || !question.trim()}
              className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 disabled:bg-slate-700
                disabled:text-slate-500 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
            >
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
              {loading ? 'Generating…' : 'Run Query'}
            </button>
          </div>
        </form>

        {/* Example chips */}
        <div className="mt-4 flex flex-wrap gap-2">
          {EXAMPLE_QUESTIONS.map((q) => (
            <button
              key={q}
              onClick={() => useExample(q)}
              className="text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700
                rounded-full px-3 py-1 transition-colors"
            >
              {q}
            </button>
          ))}
        </div>
      </div>

      {/* Error */}
      {error && (
        <div className="bg-red-500/10 border border-red-500/30 rounded-xl p-4 flex items-start gap-3">
          <AlertTriangle className="w-5 h-5 text-red-400 flex-shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-red-300">Query Failed</p>
            <p className="text-xs text-red-400 mt-1">{error}</p>
          </div>
        </div>
      )}

      {/* Results */}
      {result && (
        <div className="space-y-4">
          {/* Generated SQL */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-white flex items-center gap-2">
                <Code className="w-4 h-4 text-amber-400" />
                Generated SQL
              </h3>
              <div className="flex items-center gap-4 text-xs text-slate-500">
                <span>{result.rowCount} row{result.rowCount !== 1 ? 's' : ''}</span>
                <span>{result.executionTimeMs} ms</span>
              </div>
            </div>
            <pre className="bg-slate-950/80 border border-slate-800 rounded-lg px-4 py-3 text-xs
              text-emerald-300 font-mono overflow-x-auto whitespace-pre-wrap">
              {result.generatedSql}
            </pre>
          </div>

          {/* Results Table */}
          {result.rows && result.rows.length > 0 ? (
            <div className="bg-slate-900/60 border border-slate-800 rounded-xl overflow-hidden">
              <div className="flex items-center gap-2 px-5 py-3 border-b border-slate-800">
                <Table2 className="w-4 h-4 text-teal-400" />
                <h3 className="text-sm font-semibold text-white">
                  Results <span className="text-slate-500 font-normal">({result.rowCount} rows)</span>
                </h3>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-xs">
                  <thead>
                    <tr className="bg-slate-950/60">
                      {result.columns.map((col) => (
                        <th
                          key={col}
                          className="text-left px-4 py-2.5 text-slate-400 font-semibold border-b border-slate-800 whitespace-nowrap"
                        >
                          {col}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {result.rows.map((row, i) => (
                      <tr
                        key={i}
                        className="border-b border-slate-800/50 hover:bg-slate-800/30 transition-colors"
                      >
                        {result.columns.map((col) => (
                          <td key={col} className="px-4 py-2 text-slate-300 whitespace-nowrap font-mono">
                            {row[col] === null || row[col] === undefined ? (
                              <span className="text-slate-600 italic">null</span>
                            ) : (
                              String(row[col])
                            )}
                          </td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          ) : (
            result.rows && (
              <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-6 text-center text-slate-500 text-sm">
                Query returned 0 rows.
              </div>
            )
          )}
        </div>
      )}
    </div>
  );
}
