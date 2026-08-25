import React from 'react';
import { Database, Sparkles, ShieldCheck } from 'lucide-react';

export default function Header() {
  return (
    <header className="border-b border-slate-800 bg-slate-900/60 backdrop-blur sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-gradient-to-tr from-emerald-600 to-teal-500 rounded-xl text-white shadow-lg shadow-emerald-500/20">
            <Database className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="font-bold text-xl tracking-tight text-white">SQLGenAI</span>
              <span className="text-xs bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-2 py-0.5 rounded-full font-medium">
                Java 17 + React
              </span>
            </div>
            <p className="text-xs text-slate-400">AI-Powered Natural Language to SQL Assistant</p>
          </div>
        </div>

        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-1.5 text-xs text-slate-400 bg-slate-800/80 px-3 py-1.5 rounded-lg border border-slate-700">
            <ShieldCheck className="w-4 h-4 text-emerald-400" />
            <span>AST Security Sandbox</span>
          </div>
        </div>
      </div>
    </header>
  );
}
