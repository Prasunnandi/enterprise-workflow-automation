import React from 'react';
import { GitMerge, Ticket, Users, Clock, ShieldAlert, Cpu } from 'lucide-react';

export default function Sidebar({ activeTab, setActiveTab }) {
  const menuItems = [
    { id: 'workflows', label: 'Workflow Orchestrator', icon: GitMerge, badge: 'Standout' },
    { id: 'tickets', label: 'ServiceDesk ITSM', icon: Ticket, badge: 'P1 Live' },
    { id: 'workforce', label: 'Workforce & Shifts', icon: Users },
  ];

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col shrink-0 h-screen sticky top-0">
      <div className="p-5 border-b border-slate-800 flex items-center gap-3">
        <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-purple-600 to-violet-400 flex items-center justify-center shadow-lg shadow-purple-500/20">
          <Cpu className="h-5 w-5 text-white" />
        </div>
        <div>
          <h1 className="font-extrabold text-base tracking-tight text-white flex items-center gap-1.5">
            OPSPILOT <span className="text-xs font-semibold px-1.5 py-0.5 rounded bg-purple-500/10 text-purple-400 border border-purple-500/20">ITSM</span>
          </h1>
          <p className="text-xs text-slate-400 font-medium">Workflow Automation Suite</p>
        </div>
      </div>

      <nav className="flex-1 p-3 space-y-1.5 overflow-y-auto">
        <div className="px-3 pt-2 pb-1 text-[11px] font-semibold tracking-wider text-slate-500 uppercase">
          Automation & Operations
        </div>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl font-medium text-sm transition-all duration-200 ${
                isActive
                  ? 'bg-purple-600/15 text-purple-400 border border-purple-500/30 shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <div className="flex items-center gap-3">
                <Icon className={`h-4.5 w-4.5 ${isActive ? 'text-purple-400' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase tracking-wider ${
                  item.badge === 'Standout'
                    ? 'bg-amber-500/20 text-amber-300'
                    : 'bg-rose-500/20 text-rose-300'
                }`}>
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      <div className="p-4 m-3 rounded-xl bg-slate-950/70 border border-slate-800/80">
        <div className="text-xs font-semibold text-slate-400 mb-1">Quartz SLA Timer</div>
        <p className="text-[11px] text-slate-500 leading-relaxed">
          Active background job monitors SLA deadlines every 15s and triggers auto-escalation.
        </p>
      </div>
    </aside>
  );
}
