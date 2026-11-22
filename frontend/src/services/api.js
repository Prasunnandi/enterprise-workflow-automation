// API Client for Enterprise Service & Workflow Automation

const API_BASE = '/api/v1';

async function request(endpoint, options = {}) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`);
    return await res.json();
  } catch (err) {
    console.warn(`API call ${endpoint} failed, utilizing local reactive fallback:`, err.message);
    return getFallbackData(endpoint);
  }
}

export const api = {
  // OpsPilot Workflow Automation
  getWorkflowDefinitions: () => request('/workflows/definitions'),
  getWorkflowExecutions: () => request('/workflows/executions'),
  submitWorkflowRequest: (employeeId, workflowDefId, title, payloadJson) =>
    request(`/workflows/submit?employeeId=${employeeId}&workflowDefId=${workflowDefId}&title=${encodeURIComponent(title)}`, {
      method: 'POST',
      body: JSON.stringify(payloadJson || {})
    }),
  approveStep: (id, actor) => request(`/workflows/executions/${id}/approve?actor=${encodeURIComponent(actor || 'Department Lead')}`, { method: 'POST' }),
  escalate: (id, reason) => request(`/workflows/executions/${id}/escalate?reason=${encodeURIComponent(reason)}`, { method: 'POST' }),

  // ServiceOps ITSM Tickets
  getTickets: () => request('/tickets'),
  createTicket: (ticket) => request('/tickets', { method: 'POST', body: JSON.stringify(ticket) }),
  updateTicketStatus: (id, status) => request(`/tickets/${id}/status?status=${status}`, { method: 'PATCH' }),

  // WorkForce Employees, Shifts & Leaves
  getEmployees: () => request('/workforce/employees'),
  getDepartments: () => request('/workforce/departments'),
  getShifts: () => request('/workforce/shifts'),
  getLeaves: () => request('/workforce/leaves'),
  submitLeave: (leave) => request('/workforce/leaves', { method: 'POST', body: JSON.stringify(leave) })
};

function getFallbackData(endpoint) {
  if (endpoint.includes('/workflows/definitions')) {
    return [
      { id: 1, workflowCode: "WF-VPN-ACCESS", name: "Production VPN & Bastion Host Provisioning", defaultSlaMinutes: 45, autoAssignDepartment: { name: "IT Infrastructure & DevOps" }, description: "Automated access granting with manager authorization." },
      { id: 2, workflowCode: "WF-EQUIP-REQ", name: "Developer Laptop & Hardware Requisition", defaultSlaMinutes: 240, autoAssignDepartment: { name: "IT Infrastructure & DevOps" }, description: "Hardware intake, spec validation, and budget approval workflow." },
      { id: 3, workflowCode: "WF-LEAVE-APPROVAL", name: "Special Leave & Sabbatical Review", defaultSlaMinutes: 480, autoAssignDepartment: { name: "Human Resources & Payroll" }, description: "Multi-tier HR review and shift replacement coordination." }
    ];
  }

  if (endpoint.includes('/workflows/executions')) {
    return [
      {
        id: 1,
        executionCode: "WF-EXEC-991",
        title: "VPN Access for Contractor Data Analyst",
        currentStep: "AUTO_ASSIGNED_TO_IT-INFRA",
        status: "IN_PROGRESS",
        initiatedBy: { name: "David Miller" },
        startedAt: "2026-11-22T15:30:00",
        deadlineTimestamp: "2026-11-22T16:15:00",
        workflowDefinition: { name: "Production VPN & Bastion Host Provisioning", defaultSlaMinutes: 45, autoAssignDepartment: { name: "IT Infrastructure" } }
      },
      {
        id: 2,
        executionCode: "WF-EXEC-992",
        title: "MacBook Pro M3 Max Requisition",
        currentStep: "AUTO_ESCALATED_SLA_BREACH",
        status: "ESCALATED",
        initiatedBy: { name: "Alex Mercer" },
        startedAt: "2026-11-22T09:00:00",
        deadlineTimestamp: "2026-11-22T13:00:00",
        workflowDefinition: { name: "Developer Laptop & Hardware Requisition", defaultSlaMinutes: 240, autoAssignDepartment: { name: "IT Infrastructure" } }
      }
    ];
  }

  if (endpoint.includes('/tickets')) {
    return [
      {
        id: 1,
        ticketCode: "TKT-8801",
        title: "Production Bastion VPN Gateway Auth Failure",
        description: "Multiple developers unable to authenticate via SSO to AWS US-East VPC.",
        priority: "P1_CRITICAL",
        status: "IN_PROGRESS",
        requester: { name: "David Miller" },
        assignedDepartment: { name: "IT Infrastructure & DevOps" },
        assignee: { name: "Alex Mercer" },
        slaMinutes: 45,
        deadlineTimestamp: "2026-11-22T16:15:00",
        isEscalated: false
      },
      {
        id: 2,
        ticketCode: "TKT-7702",
        title: "PostgreSQL Read Replica Replication Lag Alert",
        description: "Replication lag on analytics replica exceeded 120 seconds.",
        priority: "P1_CRITICAL",
        status: "ESCALATED",
        requester: { name: "David Miller" },
        assignedDepartment: { name: "IT Infrastructure & DevOps" },
        assignee: { name: "Alex Mercer" },
        slaMinutes: 30,
        deadlineTimestamp: "2026-11-22T15:00:00",
        isEscalated: true,
        escalationReason: "Automatic SLA countdown timer breached. Escalated to department lead."
      },
      {
        id: 3,
        ticketCode: "TKT-5503",
        title: "Request for JetBrains All Products License",
        description: "License allocation for incoming backend engineering cohort.",
        priority: "P3_MEDIUM",
        status: "RESOLVED",
        requester: { name: "Elena Rostova" },
        assignedDepartment: { name: "IT Infrastructure & DevOps" },
        assignee: { name: "Sarah Jenkins" },
        slaMinutes: 1440,
        deadlineTimestamp: "2026-11-23T12:00:00",
        isEscalated: false
      }
    ];
  }

  if (endpoint.includes('/workforce/employees')) {
    return [
      { id: 1, empCode: "EMP-101", name: "Sarah Jenkins", email: "s.jenkins@enterprise.com", role: "DEPARTMENT_LEAD", department: { name: "IT Infrastructure" } },
      { id: 2, empCode: "EMP-102", name: "Alex Mercer", email: "a.mercer@enterprise.com", role: "TECHNICIAN", department: { name: "IT Infrastructure" } },
      { id: 3, empCode: "EMP-103", name: "Michael Chang", email: "m.chang@enterprise.com", role: "DEPARTMENT_LEAD", department: { name: "Human Resources" } },
      { id: 4, empCode: "EMP-104", name: "David Miller", email: "d.miller@enterprise.com", role: "EMPLOYEE", department: { name: "IT Infrastructure" } }
    ];
  }

  if (endpoint.includes('/workforce/shifts')) {
    return [
      { id: 1, employee: { name: "Alex Mercer" }, shiftDate: "2026-11-22", shiftType: "MORNING_PRIMARY", notes: "L2 DevOps and Ticket Lead" },
      { id: 2, employee: { name: "Sarah Jenkins" }, shiftDate: "2026-11-22", shiftType: "AFTERNOON_SHIFT", notes: "Management Escalation Lead" },
      { id: 3, employee: { name: "David Miller" }, shiftDate: "2026-11-22", shiftType: "ON_CALL_SUPPORT", notes: "PagerDuty primary on-call" }
    ];
  }

  return [];
}
