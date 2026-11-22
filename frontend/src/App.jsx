import React, { useState } from 'react';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import WorkflowOrchestrator from './pages/WorkflowOrchestrator';
import ServiceDeskTickets from './pages/ServiceDeskTickets';
import WorkforceScheduler from './pages/WorkforceScheduler';

export default function App() {
  const [activeTab, setActiveTab] = useState('workflows');
  const [refreshKey, setRefreshKey] = useState(0);

  function handleRefresh() {
    setRefreshKey(k => k + 1);
  }

  return (
    <div className="flex bg-slate-950 min-h-screen text-slate-100">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      <div className="flex-1 flex flex-col min-w-0">
        <Navbar onRefresh={handleRefresh} isRefreshing={false} />

        <main className="flex-1 p-6 max-w-7xl w-full mx-auto" key={refreshKey}>
          {activeTab === 'workflows' && <WorkflowOrchestrator />}
          {activeTab === 'tickets' && <ServiceDeskTickets />}
          {activeTab === 'workforce' && <WorkforceScheduler />}
        </main>
      </div>
    </div>
  );
}
