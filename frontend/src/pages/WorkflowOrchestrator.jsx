import React, { useState, useEffect } from 'react';
import { GitMerge, Plus, Clock, CheckCircle2, AlertTriangle, ArrowRight, ShieldAlert, UserCheck } from 'lucide-react';
import { api } from '../services/api';

export default function WorkflowOrchestrator() {
  const [definitions, setDefinitions] = useState([]);
  const [executions, setExecutions] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [showSubmitModal, setShowSubmitModal] = useState(false);
  const [selectedDefId, setSelectedDefId] = useState('');
  const [selectedEmpId, setSelectedEmpId] = useState('');
  const [requestTitle, setRequestTitle] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    const [defs, execs, emps] = await Promise.all([
      api.getWorkflowDefinitions(),
      api.getWorkflowExecutions(),
      api.getEmployees()
    ]);
    setDefinitions(defs);
    setExecutions(execs);
    setEmployees(emps);
    if (defs.length > 0) setSelectedDefId(defs[0].id);
    if (emps.length > 0) setSelectedEmpId(emps[0].id);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    await api.submitWorkflowRequest(selectedEmpId, selectedDefId, requestTitle);
    setShowSubmitModal(false);
    setRequestTitle('');
    loadData();
  }

  async function handleApprove(id) {
    await api.approveStep(id, 'IT Director');
    loadData();
  }

  async function handleEscalate(id) {
    await api.escalate(id, 'Manual escalation triggered by operations manager');
    loadData();
  }

  return (
    <div className="space-y-6">
      {/* Banner */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-slate-900 via-purple-950/20 to-slate-900 border border-purple-500/30 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-1 rounded-full text-xs font-semibold bg-purple-500/10 text-purple-400 border border-purple-500/20">
            OpsPilot Business Process Automation Engine
          </span>
          <h2 className="text-2xl font-black text-white tracking-tight mt-2">
            Automated Request Routing & SLA Escalator
          </h2>
          <p className="text-slate-400 text-sm mt-1 max-w-xl">
            When an employee submits a request, OpsPilot auto-assigns the responsible team, arms an SLA countdown timer, and auto-escalates overdue requests.
          </p>
        </div>

        <button
          onClick={() => setShowSubmitModal(true)}
          className="px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs transition flex items-center gap-2 shadow-lg shadow-purple-600/20 self-start md:self-auto"
        >
          <Plus className="h-4 w-4" />
          <span>Submit Service Request</span>
        </button>
      </div>

      {/* Active Workflow Executions */}
      <div className="space-y-4">
        <h3 className="font-bold text-white text-base">Active Workflow Pipelines ({executions.length})</h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {executions.map((x) => (
            <div key={x.id} className={`p-5 rounded-2xl bg-slate-900 border space-y-4 ${
              x.status === 'ESCALATED' ? 'border-rose-500/40 shadow-rose-950' : 'border-slate-800'
            }`}>
              <div className="flex items-start justify-between">
                <div>
                  <span className="font-mono text-xs text-purple-400 font-bold">{x.executionCode}</span>
                  <h4 className="font-extrabold text-white text-base mt-0.5">{x.title}</h4>
                  <p className="text-xs text-slate-400">Initiated by {x.initiatedBy?.name}</p>
                </div>
                <span className={`px-2.5 py-1 rounded-full text-[10px] font-extrabold uppercase tracking-wider ${
                  x.status === 'APPROVED'
                    ? 'bg-emerald-500/20 text-emerald-300'
                    : x.status === 'ESCALATED'
                    ? 'bg-rose-500/20 text-rose-300 border border-rose-500/30 animate-pulse'
                    : 'bg-purple-500/20 text-purple-300'
                }`}>
                  {x.status}
                </span>
              </div>

              {/* Progress Stepper Box */}
              <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-800 text-xs space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-slate-400 font-medium">Assigned Department:</span>
                  <span className="font-bold text-white">{x.workflowDefinition?.autoAssignDepartment?.name || 'IT Infrastructure'}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400 font-medium">Current Workflow Step:</span>
                  <span className="font-mono font-bold text-purple-400">{x.currentStep}</span>
                </div>
                <div className="flex items-center justify-between pt-2 border-t border-slate-800/80">
                  <span className="text-slate-400 font-medium flex items-center gap-1">
                    <Clock className="h-3.5 w-3.5 text-amber-400" />
                    <span>SLA Timer:</span>
                  </span>
                  <span className={`font-mono font-bold ${x.status === 'ESCALATED' ? 'text-rose-400' : 'text-amber-400'}`}>
                    {x.status === 'ESCALATED' ? 'SLA BREACHED (Escalated to Lead)' : `${x.workflowDefinition?.defaultSlaMinutes || 45} mins total`}
                  </span>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-2 pt-1">
                {x.status === 'IN_PROGRESS' && (
                  <>
                    <button
                      onClick={() => handleApprove(x.id)}
                      className="flex-1 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs transition flex items-center justify-center gap-1.5"
                    >
                      <CheckCircle2 className="h-4 w-4" />
                      <span>Approve Step</span>
                    </button>
                    <button
                      onClick={() => handleEscalate(x.id)}
                      className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-rose-950/60 border border-slate-700 hover:border-rose-500/30 text-rose-300 font-semibold text-xs transition"
                    >
                      Escalate
                    </button>
                  </>
                )}
                {x.status === 'ESCALATED' && (
                  <button
                    onClick={() => handleApprove(x.id)}
                    className="w-full py-2 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs transition flex items-center justify-center gap-1.5"
                  >
                    <span>Authorize Emergency Override Approval</span>
                  </button>
                )}
                {x.status === 'APPROVED' && (
                  <div className="w-full py-1.5 text-center text-xs font-semibold text-emerald-400 bg-emerald-500/10 rounded-xl border border-emerald-500/20">
                    Workflow Completed Successfully
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Modal for Submitting Request */}
      {showSubmitModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4">
            <h3 className="font-extrabold text-white text-base">Submit Service Request</h3>

            <form onSubmit={handleSubmit} className="space-y-3 text-xs">
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Requesting Employee</label>
                <select
                  value={selectedEmpId}
                  onChange={(e) => setSelectedEmpId(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-slate-200 focus:outline-none focus:border-purple-500"
                >
                  {employees.map(emp => (
                    <option key={emp.id} value={emp.id}>{emp.name} ({emp.empCode})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="text-slate-400 block mb-1 font-medium">Process Template</label>
                <select
                  value={selectedDefId}
                  onChange={(e) => setSelectedDefId(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-slate-200 focus:outline-none focus:border-purple-500"
                >
                  {definitions.map(def => (
                    <option key={def.id} value={def.id}>{def.name} ({def.defaultSlaMinutes} min SLA)</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="text-slate-400 block mb-1 font-medium">Request Title & Summary</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. AWS Staging Bastion Access for Analytics..."
                  value={requestTitle}
                  onChange={(e) => setRequestTitle(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-slate-200 focus:outline-none focus:border-purple-500"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setShowSubmitModal(false)}
                  className="px-4 py-2 rounded-xl text-slate-400 hover:text-slate-200 transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold transition shadow"
                >
                  Submit & Start SLA Timer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
