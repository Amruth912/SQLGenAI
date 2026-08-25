# SQLGenAI - AI-Powered Natural Language to SQL Assistant

SQLGenAI is a full-stack web application that translates natural language questions into safe, optimized, and executable PostgreSQL queries, executes them in a secure read-only sandbox, visualizes results, and provides AI-powered query explanations and automated error corrections.

---

## 🚀 Technology Stack

- **Backend**: Java 17, Spring Boot 3.3.x, Maven, Spring Web, Spring Data JPA, JSqlParser
- **Frontend**: React 18, Vite, Tailwind CSS, Axios, Lucide React, Recharts
- **Database**: PostgreSQL
- **Security**: JSqlParser AST analysis, read-only transaction sandbox, query timeouts, and strict keyword blacklists.

---

## 📋 Prerequisites

Before running the project locally, ensure you have the following installed:
1. **Java JDK 17+** (Recommended: OpenJDK 17 LTS)
2. **Apache Maven 3.9+**
3. **Node.js 18+ and npm 9+**
4. **PostgreSQL 14+** (For later database phases)

---

## 🛠️ Project Structure

```
SQLGenAI/
├── backend/                  # Spring Boot Java Application
│   ├── src/
│   │   ├── main/java/com/sqlgenai/
│   │   │   ├── config/       # App & CORS configurations
│   │   │   ├── controller/   # REST Controllers (Health, SQL, Schema, History)
│   │   │   ├── dto/          # Data Transfer Objects (Requests & Responses)
│   │   │   ├── entity/       # JPA Entities (QueryHistory, SavedQuery)
│   │   │   ├── repository/   # Spring Data JPA Repositories
│   │   │   ├── security/     # JSqlParser AST Validator & Rules
│   │   │   ├── service/      # Business services & AI clients
│   │   │   └── exception/    # Centralized exception handlers
│   │   └── resources/        # application.yml
│   └── pom.xml               # Maven configuration
│
├── frontend/                 # React + Vite + Tailwind CSS Application
│   ├── src/
│   │   ├── components/       # Reusable UI components
│   │   ├── pages/            # Page layouts & dashboards
│   │   ├── hooks/            # Custom React hooks
│   │   ├── services/         # Axios API clients
│   │   └── utils/            # Helper utilities
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore
├── .env.example
└── README.md
```

---

## ⚙️ Setup & Running Locally

### 1. Environment Configuration
Copy `.env.example` to configure variables:
```bash
cp .env.example .env
```

### 2. Run Backend (Spring Boot)
```bash
cd backend
mvn clean spring-boot:run
```
The backend API will start on `http://localhost:8080`.

**Verify Health Endpoint:**
```bash
curl http://localhost:8080/api/v1/health
```

### 3. Run Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
The frontend UI will be accessible at `http://localhost:5173`.

---

## 🧪 Testing Backend
Run the backend automated test suite:
```bash
cd backend
mvn test
```
