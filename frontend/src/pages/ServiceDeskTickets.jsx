import React, { useState, useEffect } from 'react';
import { Ticket, AlertOctagon, CheckCircle2, Clock, Filter, AlertTriangle } from 'lucide-react';
import { api } from '../services/api';

export default function ServiceDeskTickets() {
  const [tickets, setTickets] = useState([]);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    loadTickets();
  }, []);

  async function loadTickets() {
    const data = await api.getTickets();
    setTickets(data);
  }

  async function handleResolve(id) {
    await api.updateTicketStatus(id, 'RESOLVED');
    loadTickets();
  }

  const filtered = tickets.filter(t => {
    if (filter === 'ALL') return true;
    return t.priority === filter;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-extrabold text-white tracking-tight flex items-center gap-2">
            <Ticket className="h-5 w-5 text-purple-400" />
            <span>ServiceOps IT Service Management (ITSM)</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Real-time incident response, priority queues (P1-P4), technician dispatches, and SLA tracking.
          </p>
        </div>

        {/* Priority Filter */}
        <div className="flex items-center gap-1.5 p-1 bg-slate-900 border border-slate-800 rounded-xl text-xs font-semibold">
          {['ALL', 'P1_CRITICAL', 'P2_HIGH', 'P3_MEDIUM'].map(p => (
            <button
              key={p}
              onClick={() => setFilter(p)}
              className={`px-3 py-1.5 rounded-lg transition ${filter === p ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
            >
              {p.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Ticket Table */}
      <div className="rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/40">
              <th className="p-4 font-semibold">Ticket #</th>
              <th className="p-4 font-semibold">Incident Title</th>
              <th className="p-4 font-semibold">Priority</th>
              <th className="p-4 font-semibold">Requester</th>
              <th className="p-4 font-semibold">Assigned Team</th>
              <th className="p-4 font-semibold">SLA Window</th>
              <th className="p-4 font-semibold text-right">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {filtered.map((t) => (
              <tr key={t.id} className="hover:bg-slate-800/40 transition">
                <td className="p-4 font-mono font-bold text-purple-400">{t.ticketCode}</td>
                <td className="p-4">
                  <div className="font-bold text-slate-200">{t.title}</div>
                  <div className="text-[11px] text-slate-400 line-clamp-1">{t.description}</div>
                </td>
                <td className="p-4">
                  <span className={`px-2 py-0.5 rounded text-[10px] font-extrabold ${
                    t.priority === 'P1_CRITICAL'
                      ? 'bg-rose-500/20 text-rose-300 border border-rose-500/30 animate-pulse'
                      : t.priority === 'P2_HIGH'
                      ? 'bg-amber-500/20 text-amber-300'
                      : 'bg-blue-500/20 text-blue-300'
                  }`}>
                    {t.priority}
                  </span>
                </td>
                <td className="p-4 text-slate-300 font-medium">{t.requester?.name}</td>
                <td className="p-4 text-slate-300">{t.assignedDepartment?.name}</td>
                <td className="p-4 font-mono text-amber-400 font-semibold">{t.slaMinutes} mins</td>
                <td className="p-4 text-right">
                  {t.status === 'RESOLVED' ? (
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-emerald-500/20 text-emerald-300">
                      RESOLVED
                    </span>
                  ) : (
                    <button
                      onClick={() => handleResolve(t.id)}
                      className="px-3 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-[10px] transition"
                    >
                      Mark Resolved
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
