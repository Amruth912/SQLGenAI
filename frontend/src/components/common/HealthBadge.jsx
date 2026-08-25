import React from 'react';
import { CheckCircle2, AlertCircle, AlertTriangle, RefreshCw, Database } from 'lucide-react';

export default function HealthBadge({ health, loading, error, onRefresh }) {
  if (loading) {
    return (
      <div className="flex items-center space-x-2 text-xs bg-slate-800 text-slate-300 px-3 py-2 rounded-lg border border-slate-700">
        <RefreshCw className="w-3.5 h-3.5 animate-spin text-slate-400" />
        <span>Checking backend &amp; database health...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-between text-xs bg-red-500/10 text-red-400 px-3 py-2 rounded-lg border border-red-500/20">
        <div className="flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-red-400 flex-shrink-0" />
          <span>Backend offline: {error}</span>
        </div>
        <button
          onClick={onRefresh}
          className="ml-3 underline hover:text-red-300 cursor-pointer text-xs"
        >
          Retry
        </button>
      </div>
    );
  }

  const isDbConnected = health?.database?.connected;
  const isDegraded = health?.status === 'DEGRADED' || !isDbConnected;

  return (
    <div className="flex flex-wrap items-center gap-2">
      {/* Backend Status */}
      <div className="flex items-center space-x-1.5 text-xs bg-emerald-500/10 text-emerald-300 px-2.5 py-1.5 rounded-lg border border-emerald-500/20">
        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 flex-shrink-0" />
        <span>API: <strong className="font-semibold">UP</strong></span>
      </div>

      {/* Database Status */}
      <div className={`flex items-center space-x-1.5 text-xs px-2.5 py-1.5 rounded-lg border ${
        isDbConnected
          ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/20'
          : 'bg-amber-500/10 text-amber-300 border-amber-500/20'
      }`}>
        {isDbConnected ? (
          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 flex-shrink-0" />
        ) : (
          <AlertTriangle className="w-3.5 h-3.5 text-amber-400 flex-shrink-0" />
        )}
        <span>
          DB: <strong className="font-semibold">{isDbConnected ? 'CONNECTED' : 'DISCONNECTED'}</strong>
          {isDbConnected && health?.database?.latencyMs !== undefined && (
            <span className="text-slate-400 ml-1">({health.database.latencyMs}ms)</span>
          )}
        </span>
      </div>

      {/* Refresh Trigger */}
      <button
        onClick={onRefresh}
        title="Refresh Status"
        className="p-1.5 bg-slate-800 text-slate-400 hover:text-white rounded-lg border border-slate-700 transition-colors"
      >
        <RefreshCw className="w-3.5 h-3.5" />
      </button>
    </div>
  );
}
