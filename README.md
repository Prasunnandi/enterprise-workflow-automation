# ⚡ Enterprise Service & Workflow Automation Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-18-61DAFB.svg?style=for-the-badge&logo=react)](https://react.dev/)
[![Quartz](https://img.shields.io/badge/Quartz-Scheduler-red.svg?style=for-the-badge)](https://www.quartz-scheduler.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg?style=for-the-badge)](LICENSE)

An enterprise service orchestration platform merging **ServiceOps** (IT Service Management), **OpsPilot** (Business Process Automation), and **WorkForce** (Employee Shift & Attendance Planner).

---

## 📸 Working System Preview

![Enterprise Service & Workflow Automation Cockpit](docs/screenshots/dashboard.png)

## 📊 Kaggle & Production ITSM Datasets

The platform is pre-loaded with ITIL / ITSM service tickets modeled on Kaggle Enterprise Service Desk benchmarks:
- [`kaggle_itsm_service_tickets_benchmark.csv`](data/kaggle_itsm_service_tickets_benchmark.csv): Real-world service tickets across Infrastructure, Security & IAM, Database Systems, DevOps Tooling, and HR Operations, priority tags (P1–P4), SLA target response & resolution countdowns, real elapsed times, and automated Quartz escalation triggers.

---

## 📌 Architecture & Automated Pipeline

```mermaid
flowchart TD
    subgraph Frontend["React 18 + Vite UI"]
        UI_Flow["Workflow Orchestrator (OpsPilot)"]
        UI_Tickets["ServiceDesk ITSM (ServiceOps)"]
        UI_Work["Workforce & Shifts (WorkForce)"]
    end

    subgraph Backend["Spring Boot 3.3 Backend"]
        REST_API["REST Controllers & OpenAPI 3"]
        subgraph AutomationEngine["Core Automation Engine"]
            FlowService["Workflow Service (Auto-Routing)"]
            TicketService["ITSM Ticket Service"]
            SlaDaemon["Quartz SLA Escalation Daemon (15s Cron)"]
            WSBroker["STOMP WebSocket Alert Broker"]
        end
    end

    subgraph Storage["Persistence Layer"]
        DB[(PostgreSQL 16 / H2 In-Memory)]
    end

    Frontend -->|HTTP / JSON| REST_API
    REST_API --> FlowService
    REST_API --> TicketService
    FlowService --> DB
    TicketService --> DB
    SlaDaemon -->|Scan Overdue Deadlines| DB
    SlaDaemon -->|Push SLA Breaches| WSBroker
    WSBroker -->|Live Alert Push| Frontend
```

---

## 🌟 Standout Feature: Automated Request-to-Escalation Loop

When an employee submits a service request:
1. **Automated Team Assignment**: OpsPilot routes the request to the responsible department (e.g. IT Infrastructure or HR).
2. **SLA Countdown Timer Activation**: Automatically starts a countdown timer based on priority SLA rules ($P1 = 45\text{ min}$, $P2 = 120\text{ min}$).
3. **Linked Service Ticket Creation**: Generates a synchronized ITSM ticket on the technician dispatch board.
4. **Automated Overdue Escalation**: A background Quartz scheduler checks active requests every 15 seconds; if a deadline expires without resolution, it triggers auto-escalation to the department lead and broadcasts a real-time alert over WebSockets.

---

## 🏢 Core Modules

* **OpsPilot (Process Automation)**: Reusable process templates, multi-tier approvals, and execution audit history.
* **ServiceOps (ITSM)**: Incident triage, priority classifications (P1 Critical through P4 Low), technician assignments, and resolution logging.
* **WorkForce (Shift & Attendance Planner)**: Employee department directory, shift allocations (Morning, Afternoon, Night On-Call), and leave requests.

---

## 🛠️ Tech Stack

* **Backend**: Java 21, Spring Boot 3.3.4, Spring Data JPA, Spring WebSocket (STOMP), Quartz Scheduler, Springdoc OpenAPI 3.0
* **Database**: PostgreSQL 16 / H2 In-Memory
* **Frontend**: React 18, Vite 5, Tailwind CSS, Lucide React
* **Deployment**: Docker, Docker Compose, Multi-stage builds

---

## 📡 REST API Reference

Swagger UI accessible at:
`http://localhost:8082/swagger-ui.html`

### Workflow Endpoints
* `GET /api/v1/workflows/definitions` — List process templates.
* `GET /api/v1/workflows/executions` — Active and completed workflow pipelines.
* `POST /api/v1/workflows/submit` — Submit request (starts SLA timer and auto-assigns team).
* `POST /api/v1/workflows/executions/{id}/approve` — Approve step.
* `POST /api/v1/workflows/executions/{id}/escalate` — Trigger escalation.

### ServiceOps ITSM Endpoints
* `GET /api/v1/tickets` — List service tickets.
* `POST /api/v1/tickets` — Log incident ticket.
* `PATCH /api/v1/tickets/{id}/status` — Update resolution status.

---

## ⚡ Quickstart

### Running with Docker Compose:
```bash
docker-compose up --build
```
* **Frontend**: `http://localhost:3002`
* **Backend API**: `http://localhost:8082`
* **Swagger UI**: `http://localhost:8082/swagger-ui.html`

### Running Locally:
```bash
# 1. Start Backend
cd backend
mvn clean spring-boot:run

# 2. Start Frontend
cd frontend
npm install
npm run dev
```

---

## 👨‍💻 Author

**Prasun Nandi**
* GitHub: [@Prasunnandi](https://github.com/Prasunnandi)
* Repository: [enterprise-workflow-automation](https://github.com/Prasunnandi/enterprise-workflow-automation)

---

## 📄 License

Licensed under the [MIT License](LICENSE).
