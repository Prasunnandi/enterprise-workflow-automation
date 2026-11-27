/**
 * Enterprise Multi-Level Expense Approval Rule Engine
 * Evaluates conditional policy matrices, multi-tiered escalation chains,
 * financial delegation-of-authority (DOA) limits, and parallel approval workflows.
 */

export const APPROVAL_STATUS = {
  SUBMITTED: 'SUBMITTED',
  IN_REVIEW: 'IN_REVIEW',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
  ESCALATED: 'ESCALATED',
  AUTO_APPROVED: 'AUTO_APPROVED'
};

export const APPROVAL_ROLES = {
  DIRECT_MANAGER: 'DIRECT_MANAGER',
  FINANCE_ANALYST: 'FINANCE_ANALYST',
  DEPARTMENT_DIRECTOR: 'DEPARTMENT_DIRECTOR',
  VP_EXECUTIVE: 'VP_EXECUTIVE',
  CFO: 'CFO',
  CEO: 'CEO'
};

/**
 * Standard Corporate Expense Policy Matrix
 */
export const DEFAULT_EXPENSE_TIERS = [
  {
    tier: 0,
    maxAmountUsd: 500,
    requiredRoles: [],
    autoApprove: true,
    description: 'De minimis operational expense - Auto approved'
  },
  {
    tier: 1,
    maxAmountUsd: 5000,
    requiredRoles: [APPROVAL_ROLES.DIRECT_MANAGER],
    autoApprove: false,
    description: 'Standard operational expense - Direct Manager approval'
  },
  {
    tier: 2,
    maxAmountUsd: 25000,
    requiredRoles: [APPROVAL_ROLES.DIRECT_MANAGER, APPROVAL_ROLES.DEPARTMENT_DIRECTOR],
    autoApprove: false,
    description: 'Capital expense - Director and Manager dual sign-off'
  },
  {
    tier: 3,
    maxAmountUsd: 100000,
    requiredRoles: [
      APPROVAL_ROLES.DIRECT_MANAGER,
      APPROVAL_ROLES.DEPARTMENT_DIRECTOR,
      APPROVAL_ROLES.FINANCE_ANALYST,
      APPROVAL_ROLES.VP_EXECUTIVE
    ],
    autoApprove: false,
    description: 'Major procurement - Executive VP & Finance verification'
  },
  {
    tier: 4,
    maxAmountUsd: Infinity,
    requiredRoles: [
      APPROVAL_ROLES.DEPARTMENT_DIRECTOR,
      APPROVAL_ROLES.VP_EXECUTIVE,
      APPROVAL_ROLES.CFO
    ],
    autoApprove: false,
    description: 'Strategic expenditure - CFO approval required'
  }
];

export class ExpenseApprovalRuleEngine {
  /**
   * @param {Object} [options]
   * @param {Array<Object>} [options.tiers] - Custom approval tiers
   */
  constructor(options = {}) {
    this.tiers = options.tiers || DEFAULT_EXPENSE_TIERS;
    this.requests = new Map(); // requestId -> ApprovalState
  }

  /**
   * Resolve approval tier for a requested amount
   * @param {number} amountUsd
   * @returns {Object} Applicable tier policy
   */
  resolveTier(amountUsd) {
    const amt = Number(amountUsd);
    if (isNaN(amt) || amt < 0) {
      throw new Error(`Invalid expense amount: "${amountUsd}"`);
    }

    for (const tier of this.tiers) {
      if (amt <= tier.maxAmountUsd) {
        return tier;
      }
    }
    return this.tiers[this.tiers.length - 1];
  }

  /**
   * Submit an expense request and initialize approval chain
   * @param {Object} expenseRequest
   * @param {string} expenseRequest.id
   * @param {string} expenseRequest.submitterName
   * @param {string} expenseRequest.department
   * @param {number} expenseRequest.amountUsd
   * @param {string} expenseRequest.category - 'TRAVEL', 'SOFTWARE', 'HARDWARE', 'CONSULTING'
   * @param {boolean} [expenseRequest.isOverBudget=false]
   * @returns {Object} Initialized approval workflow instance
   */
  submitExpense(expenseRequest) {
    const {
      id = `EXP-${Date.now()}`,
      submitterName,
      department,
      amountUsd,
      category = 'OPERATIONAL',
      isOverBudget = false
    } = expenseRequest;

    const tier = this.resolveTier(amountUsd);

    // If request exceeds budget, dynamically inject Finance Analyst approval if not present
    let roles = [...tier.requiredRoles];
    if (isOverBudget && !roles.includes(APPROVAL_ROLES.FINANCE_ANALYST)) {
      roles.push(APPROVAL_ROLES.FINANCE_ANALYST);
    }

    const steps = roles.map((role, idx) => ({
      stepIndex: idx,
      role,
      status: 'PENDING',
      approverName: null,
      decisionTimestamp: null,
      comments: null
    }));

    const status = tier.autoApprove && !isOverBudget
      ? APPROVAL_STATUS.AUTO_APPROVED
      : APPROVAL_STATUS.IN_REVIEW;

    const record = {
      requestId: id,
      submitterName,
      department,
      amountUsd: Number(amountUsd),
      category,
      isOverBudget,
      tier: tier.tier,
      tierDescription: tier.description,
      status,
      currentStepIndex: tier.autoApprove && !isOverBudget ? steps.length : 0,
      totalSteps: steps.length,
      steps,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    this.requests.set(id, record);
    return record;
  }

  /**
   * Process an approval or rejection decision at current step
   * @param {string} requestId
   * @param {string} approverRole - Role of the decision maker
   * @param {string} decision - 'APPROVE' | 'REJECT'
   * @param {string} approverName
   * @param {string} [comments]
   * @returns {Object} Updated approval instance
   */
  processDecision(requestId, approverRole, decision, approverName, comments = '') {
    const req = this.requests.get(requestId);
    if (!req) {
      throw new Error(`Expense request "${requestId}" not found.`);
    }

    if (req.status === APPROVAL_STATUS.APPROVED || req.status === APPROVAL_STATUS.AUTO_APPROVED) {
      throw new Error(`Request "${requestId}" is already approved.`);
    }
    if (req.status === APPROVAL_STATUS.REJECTED) {
      throw new Error(`Request "${requestId}" has already been rejected.`);
    }

    const currentStep = req.steps[req.currentStepIndex];
    if (!currentStep) {
      throw new Error(`No active pending step found for request "${requestId}".`);
    }

    if (currentStep.role !== approverRole) {
      throw new Error(
        `Role mismatch: expected "${currentStep.role}" approval at step ${req.currentStepIndex}, received "${approverRole}".`
      );
    }

    currentStep.approverName = approverName;
    currentStep.decisionTimestamp = new Date().toISOString();
    currentStep.comments = comments;

    if (decision === 'REJECT') {
      currentStep.status = 'REJECTED';
      req.status = APPROVAL_STATUS.REJECTED;
      req.updatedAt = new Date().toISOString();
      return req;
    }

    if (decision === 'APPROVE') {
      currentStep.status = 'APPROVED';
      req.currentStepIndex++;

      // Check if all steps completed
      if (req.currentStepIndex >= req.steps.length) {
        req.status = APPROVAL_STATUS.APPROVED;
      }
      req.updatedAt = new Date().toISOString();
      return req;
    }

    throw new Error(`Invalid decision "${decision}". Expected "APPROVE" or "REJECT".`);
  }

  /**
   * Retrieve request state
   */
  getRequest(requestId) {
    return this.requests.get(requestId) || null;
  }
}

// Dual ESM & CommonJS compatibility
if (typeof module !== 'undefined' && module.exports) {
  module.exports = {
    APPROVAL_STATUS,
    APPROVAL_ROLES,
    DEFAULT_EXPENSE_TIERS,
    ExpenseApprovalRuleEngine
  };
}

export default ExpenseApprovalRuleEngine;
