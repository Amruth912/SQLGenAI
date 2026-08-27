import React, { useState, useEffect } from 'react';
import Header from '../components/common/Header';
import HealthBadge from '../components/common/HealthBadge';
import QueryPanel from '../components/query/QueryPanel';
import { healthService } from '../services/healthService';
import { Sparkles, Database, Activity, CheckCircle2, AlertTriangle, Layers } from 'lucide-react';

export default function DashboardPage() {
  const [health, setHealth] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const checkHealth = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await healthService.getHealth();
      setHealth(data);
    } catch (err) {
      setError(err.message || 'Could not connect to backend');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    checkHealth();
  }, []);

  const isDbConnected = health?.database?.connected;

  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
      <Header />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">

        {/* Phase 4 Status Banner */}
        <div className="bg-gradient-to-r from-indigo-950/50 via-slate-900 to-slate-900 border border-indigo-800/40 rounded-2xl p-6 shadow-xl">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <div className="flex items-center space-x-2">
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                  Phase 4 Active
                </span>
                <span className="text-slate-400 text-xs">Natural Language → SQL AI Service</span>
              </div>
              <h1 className="text-2xl font-extrabold text-white tracking-tight mt-2">
                SQLGenAI — Natural Language to SQL
              </h1>
              <p className="text-slate-400 text-sm mt-1 max-w-2xl">
                Ask a question or describe a data operation in plain English. The AI generates safe, validated PostgreSQL queries
                (SELECT, INSERT, UPDATE, DELETE, DDL), requires confirmation for mutations, and executes live.
              </p>
            </div>
            <div className="flex-shrink-0">
              <HealthBadge
                health={health}
                loading={loading}
                error={error}
                onRefresh={checkHealth}
              />
            </div>
          </div>
        </div>

        {/* Status Row */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-4 flex items-center gap-3">
            <Database className="w-5 h-5 text-emerald-400 flex-shrink-0" />
            <div>
              <p className="text-xs text-slate-500">PostgreSQL</p>
              <p className={`text-sm font-medium ${isDbConnected ? 'text-emerald-400' : 'text-amber-400'}`}>
                {loading ? 'Checking…' : isDbConnected ? 'Connected' : 'Offline'}
              </p>
            </div>
            {!loading && (isDbConnected
              ? <CheckCircle2 className="w-4 h-4 text-emerald-500 ml-auto" />
              : <AlertTriangle className="w-4 h-4 text-amber-400 ml-auto" />
            )}
          </div>

          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-4 flex items-center gap-3">
            <Sparkles className="w-5 h-5 text-indigo-400 flex-shrink-0" />
            <div>
              <p className="text-xs text-slate-500">AI Provider</p>
              <p className="text-sm font-medium text-indigo-300">Gemini Flash</p>
            </div>
            <CheckCircle2 className="w-4 h-4 text-emerald-500 ml-auto" />
          </div>

          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-4 flex items-center gap-3">
            <Layers className="w-5 h-5 text-teal-400 flex-shrink-0" />
            <div>
              <p className="text-xs text-slate-500">SQL Validator</p>
              <p className="text-sm font-medium text-teal-300">JSqlParser (Full SQL AST Validator)</p>
            </div>
            <CheckCircle2 className="w-4 h-4 text-emerald-500 ml-auto" />
          </div>
        </div>

        {/* Main Query Panel */}
        <QueryPanel />

      </main>

      <footer className="border-t border-slate-900 py-6 text-center text-xs text-slate-600">
        SQLGenAI &copy; 2026 &bull; Java 17 + Spring Boot 3.3.3 + PostgreSQL + React + Gemini AI
      </footer>
    </div>
  );
}
