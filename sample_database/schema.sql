-- SQLGenAI Sample Enterprise HR & Sales Database Schema
-- Compatible with PostgreSQL 14+

DROP TABLE IF EXISTS salaries_history CASCADE;
DROP TABLE IF EXISTS employee_projects CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;

-- 1. Departments Table
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(100)
);

-- 2. Employees Table
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL REFERENCES departments(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    job_title VARCHAR(100),
    hire_date DATE,
    salary NUMERIC(12,2),
    manager_id BIGINT REFERENCES employees(id)
);

-- 3. Projects Table
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    budget NUMERIC(14,2)
);

-- 4. Employee-Projects Mapping Table (Many-to-Many)
CREATE TABLE employee_projects (
    employee_id BIGINT NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    role VARCHAR(100),
    assigned_date DATE,
    PRIMARY KEY (employee_id, project_id)
);

-- 5. Salaries History Table (Audit & Time Series)
CREATE TABLE salaries_history (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    salary NUMERIC(12,2) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE
);

-- Indexes for Query Performance and Relational Lookups
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_manager_id ON employees(manager_id);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employee_projects_project_id ON employee_projects(project_id);
CREATE INDEX idx_salaries_history_employee_id ON salaries_history(employee_id);
CREATE INDEX idx_salaries_history_effective_from ON salaries_history(effective_from);
