import React, { useState, useEffect } from 'react';
import { Users, Calendar, Clock, CheckCircle2, UserCheck, ShieldCheck } from 'lucide-react';
import { api } from '../services/api';

export default function WorkforceScheduler() {
  const [employees, setEmployees] = useState([]);
  const [shifts, setShifts] = useState([]);
  const [leaves, setLeaves] = useState([]);
  const [activeTab, setActiveTab] = useState('shifts');

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    const [e, s, l] = await Promise.all([
      api.getEmployees(),
      api.getShifts(),
      api.getLeaves()
    ]);
    setEmployees(e);
    setShifts(s);
    setLeaves(l);
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-extrabold text-white tracking-tight flex items-center gap-2">
            <Users className="h-5 w-5 text-purple-400" />
            <span>WorkForce Shift & Attendance Planner</span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Manage employee shifts, on-call standby rotations, and annual leave coordination.
          </p>
        </div>

        <div className="flex items-center gap-1.5 p-1 bg-slate-900 border border-slate-800 rounded-xl text-xs font-semibold">
          <button
            onClick={() => setActiveTab('shifts')}
            className={`px-3 py-1.5 rounded-lg transition ${activeTab === 'shifts' ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
          >
            Shift Roster
          </button>
          <button
            onClick={() => setActiveTab('employees')}
            className={`px-3 py-1.5 rounded-lg transition ${activeTab === 'employees' ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
          >
            Employee Directory ({employees.length})
          </button>
          <button
            onClick={() => setActiveTab('leaves')}
            className={`px-3 py-1.5 rounded-lg transition ${activeTab === 'leaves' ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
          >
            Leave Requests ({leaves.length})
          </button>
        </div>
      </div>

      {/* Shifts View */}
      {activeTab === 'shifts' && (
        <div className="rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/40">
                <th className="p-4 font-semibold">Employee</th>
                <th className="p-4 font-semibold">Shift Date</th>
                <th className="p-4 font-semibold">Assigned Shift</th>
                <th className="p-4 font-semibold text-right">Operational Role / Notes</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {shifts.map((s) => (
                <tr key={s.id} className="hover:bg-slate-800/40 transition">
                  <td className="p-4 font-bold text-slate-200">{s.employee?.name}</td>
                  <td className="p-4 font-mono text-slate-300">{s.shiftDate}</td>
                  <td className="p-4">
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-purple-500/20 text-purple-300">
                      {s.shiftType.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="p-4 text-slate-400 text-right">{s.notes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Employees View */}
      {activeTab === 'employees' && (
        <div className="rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/40">
                <th className="p-4 font-semibold">Code & Name</th>
                <th className="p-4 font-semibold">Email</th>
                <th className="p-4 font-semibold">Department</th>
                <th className="p-4 font-semibold text-right">Role</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {employees.map((e) => (
                <tr key={e.id} className="hover:bg-slate-800/40 transition">
                  <td className="p-4">
                    <div className="font-bold text-slate-200">{e.name}</div>
                    <div className="font-mono text-[11px] text-slate-400">{e.empCode}</div>
                  </td>
                  <td className="p-4 text-slate-300 font-mono">{e.email}</td>
                  <td className="p-4 text-slate-300">{e.department?.name}</td>
                  <td className="p-4 text-right">
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-blue-500/20 text-blue-300">
                      {e.role}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Leaves View */}
      {activeTab === 'leaves' && (
        <div className="rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/40">
                <th className="p-4 font-semibold">Employee</th>
                <th className="p-4 font-semibold">Dates</th>
                <th className="p-4 font-semibold">Leave Type</th>
                <th className="p-4 font-semibold">Reason</th>
                <th className="p-4 font-semibold text-right">Approval Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {leaves.map((l) => (
                <tr key={l.id} className="hover:bg-slate-800/40 transition">
                  <td className="p-4 font-bold text-slate-200">{l.employee?.name}</td>
                  <td className="p-4 font-mono text-slate-300">{l.startDate} ➔ {l.endDate}</td>
                  <td className="p-4 text-slate-300">{l.leaveType}</td>
                  <td className="p-4 text-slate-400">{l.reason}</td>
                  <td className="p-4 text-right">
                    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                      l.status === 'APPROVED' ? 'bg-emerald-500/20 text-emerald-300' : 'bg-amber-500/20 text-amber-300'
                    }`}>
                      {l.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
