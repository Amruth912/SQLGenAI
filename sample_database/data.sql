-- SQLGenAI Sample Enterprise HR & Sales Dataset
-- Clean transactional insertion with valid foreign keys and hierarchical relationships

-- 1. Insert Departments (8 departments)
INSERT INTO departments (id, name, location) VALUES
(1, 'Engineering', 'San Francisco, CA'),
(2, 'Product Management', 'San Francisco, CA'),
(3, 'Sales', 'New York, NY'),
(4, 'Marketing', 'New York, NY'),
(5, 'Finance', 'Chicago, IL'),
(6, 'Human Resources', 'Austin, TX'),
(7, 'Customer Support', 'Austin, TX'),
(8, 'Legal & Compliance', 'Washington, DC');

SELECT setval('departments_id_seq', (SELECT MAX(id) FROM departments));

-- 2. Insert Employees (35 employees with hierarchical reporting structure)
-- Leadership (Executives)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(1, 1, 'Marcus', 'Vance', 'marcus.vance@company.com', 'VP of Engineering', '2018-03-15', 215000.00, NULL),
(2, 2, 'Elena', 'Rostova', 'elena.rostova@company.com', 'VP of Product', '2018-06-01', 205000.00, NULL),
(3, 3, 'David', 'Sterling', 'david.sterling@company.com', 'VP of Global Sales', '2019-01-10', 198000.00, NULL),
(4, 5, 'Victoria', 'Chen', 'victoria.chen@company.com', 'VP of Finance', '2018-11-20', 195000.00, NULL);

-- Engineering Team (Reporting to Marcus Vance #1 or Team Leads #5, #6)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(5, 1, 'Alexander', 'Wright', 'alex.wright@company.com', 'Principal Software Architect', '2019-04-12', 178000.00, 1),
(6, 1, 'Sophia', 'Patel', 'sophia.patel@company.com', 'Engineering Manager - Core Systems', '2019-08-01', 165000.00, 1),
(7, 1, 'Liam', 'O''Connor', 'liam.oconnor@company.com', 'Senior Backend Engineer', '2020-02-15', 145000.00, 6),
(8, 1, 'Maya', 'Lin', 'maya.lin@company.com', 'Senior Full Stack Engineer', '2020-05-10', 142000.00, 6),
(9, 1, 'James', 'Kowalski', 'james.kowalski@company.com', 'DevOps & Cloud Architect', '2020-09-01', 152000.00, 5),
(10, 1, 'Lucas', 'Silva', 'lucas.silva@company.com', 'Software Engineer II', '2021-03-20', 118000.00, 6),
(11, 1, 'Chloe', 'Dubois', 'chloe.dubois@company.com', 'Frontend Engineer', '2021-07-15', 112000.00, 6),
(12, 1, 'Zack', 'Taylor', 'zack.taylor@company.com', 'Junior Software Engineer', '2023-01-09', 88000.00, 7),
(13, 1, 'Amina', 'Diallo', 'amina.diallo@company.com', 'QA Lead Engineer', '2020-11-15', 128000.00, 6);

-- Product Management Team (Reporting to Elena Rostova #2)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(14, 2, 'Benjamin', 'Hayes', 'benjamin.hayes@company.com', 'Director of Product Management', '2019-05-01', 168000.00, 2),
(15, 2, 'Priya', 'Sharma', 'priya.sharma@company.com', 'Senior Technical Product Manager', '2020-08-15', 142000.00, 14),
(16, 2, 'Oliver', 'Bennett', 'oliver.bennett@company.com', 'Product Designer / UX Lead', '2021-02-01', 125000.00, 14),
(17, 2, 'Hannah', 'Abbott', 'hannah.abbott@company.com', 'Product Data Analyst', '2022-04-18', 98000.00, 14);

-- Sales & Business Development (Reporting to David Sterling #3)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(18, 3, 'Gregory', 'House', 'gregory.house@company.com', 'Enterprise Sales Director', '2019-09-15', 155000.00, 3),
(19, 3, 'Jessica', 'Pearson', 'jessica.pearson@company.com', 'Senior Account Executive', '2020-03-01', 125000.00, 18),
(20, 3, 'Harvey', 'Specter', 'harvey.specter@company.com', 'Enterprise Account Executive', '2021-01-15', 120000.00, 18),
(21, 3, 'Rachel', 'Zane', 'rachel.zane@company.com', 'Sales Development Representative Lead', '2021-09-01', 82000.00, 18),
(22, 3, 'Michael', 'Ross', 'michael.ross@company.com', 'Sales Development Representative', '2022-10-10', 65000.00, 21);

-- Marketing Team (Reporting to David Sterling #3 or Elena Rostova #2)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(23, 4, 'Sarah', 'Connor', 'sarah.connor@company.com', 'Director of Growth Marketing', '2019-10-01', 148000.00, 3),
(24, 4, 'Daniel', 'LaRusso', 'daniel.larusso@company.com', 'Content Marketing Specialist', '2021-06-15', 85000.00, 23),
(25, 4, 'Samantha', 'Jones', 'samantha.jones@company.com', 'Brand & Events Manager', '2020-04-01', 105000.00, 23);

-- Finance Team (Reporting to Victoria Chen #4)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(26, 5, 'Julian', 'Assange', 'julian.assange@company.com', 'Senior Financial Controller', '2019-12-01', 135000.00, 4),
(27, 5, 'Grace', 'Hopper', 'grace.hopper@company.com', 'Staff Accountant', '2021-05-15', 88000.00, 26),
(28, 5, 'Alan', 'Turing', 'alan.turing@company.com', 'Senior Financial Analyst', '2020-07-01', 115000.00, 26);

-- Human Resources Team (Reporting to Marcus Vance #1 / Executive)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(29, 6, 'Evelyn', 'Cross', 'evelyn.cross@company.com', 'Head of People Operations', '2019-02-01', 145000.00, 1),
(30, 6, 'Nathan', 'Drake', 'nathan.drake@company.com', 'Lead Technical Recruiter', '2020-06-15', 98000.00, 29),
(31, 6, 'Lara', 'Croft', 'lara.croft@company.com', 'HR Business Partner', '2021-11-01', 89000.00, 29);

-- Customer Support Team (Reporting to Elena Rostova #2)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(32, 7, 'Carlos', 'Mendoza', 'carlos.mendoza@company.com', 'Customer Support Operations Lead', '2020-01-15', 88000.00, 2),
(33, 7, 'Fatima', 'Al-Mansoor', 'fatima.mansoor@company.com', 'Senior Support Engineer', '2021-04-10', 74000.00, 32),
(34, 7, 'Derek', 'Zoolander', 'derek.zoolander@company.com', 'Technical Support Specialist', '2022-08-01', 62000.00, 32);

-- Legal & Compliance Team (Reporting to Victoria Chen #4)
INSERT INTO employees (id, department_id, first_name, last_name, email, job_title, hire_date, salary, manager_id) VALUES
(35, 8, 'Eleanor', 'Vance', 'eleanor.vance@company.com', 'Chief Legal Counsel', '2019-01-05', 185000.00, 4);

SELECT setval('employees_id_seq', (SELECT MAX(id) FROM employees));

-- 3. Insert Projects (12 projects with diverse dates, statuses, and budgets)
INSERT INTO projects (id, name, description, start_date, end_date, budget) VALUES
(1, 'Cloud Migration Platform', 'Migrate legacy on-prem infrastructure to multi-region cloud deployment', '2023-01-15', '2023-12-31', 450000.00),
(2, 'AI Customer Assistant', 'Natural language virtual assistant for self-service support and deflection', '2023-04-01', '2024-06-30', 320000.00),
(3, 'Payment Gateway Redesign', 'Zero-downtime integration of global payment methods and fraud detection', '2023-02-10', '2023-10-15', 280000.00),
(4, 'Mobile App 3.0', 'Complete rewrite in React Native with offline sync capabilities', '2023-06-01', '2024-03-31', 390000.00),
(5, 'SOC2 Security Compliance 2024', 'Annual enterprise audit, penetration testing, and IAM automation', '2023-09-01', '2024-02-28', 150000.00),
(6, 'Enterprise Data Warehouse', 'Snowflake-based ETL ingestion pipeline and real-time executive dashboard', '2023-03-15', '2024-05-30', 520000.00),
(7, 'Global Sales Automation', 'Salesforce CRM workflows and automated outbound sequencing pipeline', '2023-05-01', '2023-11-30', 180000.00),
(8, 'Q3 Brand Revamp Campaign', 'Brand identity update, digital advertising campaigns, and website redesign', '2023-07-01', '2023-09-30', 210000.00),
(9, 'Internal HR Portal', 'Self-service employee benefits, performance reviews, and PTO tracking app', '2023-08-15', '2024-01-31', 120000.00),
(10, 'Supply Chain API Connector', 'REST and GraphQL APIs for B2B supplier integration', '2024-01-10', '2024-09-30', 310000.00),
(11, 'Next-Gen Analytics Engine', 'High-throughput event streaming with Apache Kafka and ClickHouse', '2024-02-01', '2024-12-15', 600000.00),
(12, 'Automated Support Deflection', 'Ticket auto-categorization and smart response generation', '2024-03-01', '2024-08-31', 160000.00);

SELECT setval('projects_id_seq', (SELECT MAX(id) FROM projects));

-- 4. Insert Employee Projects (65 assignments mapping staff to projects)
INSERT INTO employee_projects (employee_id, project_id, role, assigned_date) VALUES
-- Project 1: Cloud Migration Platform
(1, 1, 'Executive Sponsor', '2023-01-15'),
(5, 1, 'Lead Architect', '2023-01-15'),
(9, 1, 'DevOps Lead', '2023-01-15'),
(7, 1, 'Backend Engineer', '2023-02-01'),
(10, 1, 'Infrastructure Engineer', '2023-02-15'),
(13, 1, 'QA Automation Engineer', '2023-03-01'),

-- Project 2: AI Customer Assistant
(2, 2, 'Product Sponsor', '2023-04-01'),
(15, 2, 'Lead Product Manager', '2023-04-01'),
(5, 2, 'AI/ML Architect', '2023-04-10'),
(8, 2, 'Full Stack Engineer', '2023-04-15'),
(11, 2, 'UI Developer', '2023-05-01'),
(17, 2, 'Data Analyst', '2023-05-15'),
(33, 2, 'Domain Specialist', '2023-04-01'),

-- Project 3: Payment Gateway Redesign
(6, 3, 'Technical Project Lead', '2023-02-10'),
(7, 3, 'Core Payments Developer', '2023-02-15'),
(10, 3, 'Backend Engineer', '2023-03-01'),
(13, 3, 'Security QA Engineer', '2023-03-10'),
(14, 3, 'Product Manager', '2023-02-10'),
(28, 3, 'Financial Auditor', '2023-02-10'),

-- Project 4: Mobile App 3.0
(8, 4, 'Mobile Lead', '2023-06-01'),
(11, 4, 'Frontend Engineer', '2023-06-01'),
(12, 4, 'Junior Mobile Developer', '2023-06-15'),
(16, 4, 'Lead UX Designer', '2023-06-01'),
(15, 4, 'Product Manager', '2023-06-01'),
(13, 4, 'Mobile QA Specialist', '2023-07-01'),

-- Project 5: SOC2 Security Compliance
(9, 5, 'Security Architect', '2023-09-01'),
(6, 5, 'Systems Lead', '2023-09-01'),
(35, 5, 'Legal Counsel', '2023-09-01'),
(26, 5, 'Compliance Auditor', '2023-09-15'),

-- Project 6: Enterprise Data Warehouse
(4, 6, 'Executive Sponsor', '2023-03-15'),
(5, 6, 'Data Architect', '2023-03-15'),
(7, 6, 'ETL Engineer', '2023-04-01'),
(17, 6, 'BI Developer', '2023-04-01'),
(28, 6, 'Financial Analyst', '2023-04-15'),
(14, 6, 'Product Owner', '2023-03-15'),

-- Project 7: Global Sales Automation
(3, 7, 'Executive Sponsor', '2023-05-01'),
(18, 7, 'Project Lead', '2023-05-01'),
(19, 7, 'Enterprise Sales Rep', '2023-05-15'),
(20, 7, 'Sales Specialist', '2023-05-15'),
(21, 7, 'SDR Operations Lead', '2023-06-01'),
(8, 7, 'Integration Engineer', '2023-06-01'),

-- Project 8: Q3 Brand Revamp Campaign
(23, 8, 'Campaign Director', '2023-07-01'),
(24, 8, 'Content Writer', '2023-07-01'),
(25, 8, 'Events Coordinator', '2023-07-01'),
(16, 8, 'Brand Designer', '2023-07-05'),

-- Project 9: Internal HR Portal
(29, 9, 'Project Sponsor', '2023-08-15'),
(30, 9, 'Recruiting Lead', '2023-08-15'),
(31, 9, 'HR Coordinator', '2023-08-15'),
(11, 9, 'Frontend Engineer', '2023-09-01'),
(12, 9, 'Junior Developer', '2023-09-01'),

-- Project 10: Supply Chain API Connector
(6, 10, 'Engineering Lead', '2024-01-10'),
(7, 10, 'API Developer', '2024-01-15'),
(10, 10, 'Backend Developer', '2024-01-20'),
(15, 10, 'Product Manager', '2024-01-10'),
(35, 10, 'Contracts Reviewer', '2024-01-15'),

-- Project 11: Next-Gen Analytics Engine
(1, 11, 'Executive Lead', '2024-02-01'),
(5, 11, 'Chief Architect', '2024-02-01'),
(9, 11, 'Streaming Infra Lead', '2024-02-01'),
(7, 11, 'Senior Backend Engineer', '2024-02-15'),
(17, 11, 'Analytics Specialist', '2024-02-15'),

-- Project 12: Automated Support Deflection
(32, 12, 'Operations Lead', '2024-03-01'),
(33, 12, 'Support Engineer', '2024-03-01'),
(34, 12, 'Support Specialist', '2024-03-01'),
(8, 12, 'AI Integration Developer', '2024-03-10');

-- 5. Insert Salaries History (55 historical salary records tracking career progression)
INSERT INTO salaries_history (employee_id, salary, effective_from, effective_to) VALUES
-- Marcus Vance (VP Eng)
(1, 185000.00, '2018-03-15', '2020-03-14'),
(1, 200000.00, '2020-03-15', '2022-03-14'),
(1, 215000.00, '2022-03-15', NULL),

-- Elena Rostova (VP Product)
(2, 175000.00, '2018-06-01', '2020-05-31'),
(2, 190000.00, '2020-06-01', '2022-05-31'),
(2, 205000.00, '2022-06-01', NULL),

-- David Sterling (VP Sales)
(3, 170000.00, '2019-01-10', '2021-01-09'),
(3, 185000.00, '2021-01-10', '2023-01-09'),
(3, 198000.00, '2023-01-10', NULL),

-- Victoria Chen (VP Finance)
(4, 168000.00, '2018-11-20', '2021-02-28'),
(4, 182000.00, '2021-03-01', '2023-02-28'),
(4, 195000.00, '2023-03-01', NULL),

-- Alexander Wright (Principal Architect)
(5, 150000.00, '2019-04-12', '2021-04-11'),
(5, 165000.00, '2021-04-12', '2023-04-11'),
(5, 178000.00, '2023-04-12', NULL),

-- Sophia Patel (Eng Manager)
(6, 138000.00, '2019-08-01', '2021-07-31'),
(6, 152000.00, '2021-08-01', '2023-07-31'),
(6, 165000.00, '2023-08-01', NULL),

-- Liam O'Connor (Senior Backend)
(7, 125000.00, '2020-02-15', '2022-02-14'),
(7, 135000.00, '2022-02-15', '2023-08-31'),
(7, 145000.00, '2023-09-01', NULL),

-- Maya Lin (Senior Full Stack)
(8, 120000.00, '2020-05-10', '2022-05-09'),
(8, 132000.00, '2022-05-10', '2023-11-30'),
(8, 142000.00, '2023-12-01', NULL),

-- James Kowalski (DevOps Architect)
(9, 130000.00, '2020-09-01', '2022-08-31'),
(9, 142000.00, '2022-09-01', '2024-01-31'),
(9, 152000.00, '2024-02-01', NULL),

-- Lucas Silva (Software Engineer II)
(10, 102000.00, '2021-03-20', '2023-03-19'),
(10, 118000.00, '2023-03-20', NULL),

-- Chloe Dubois (Frontend)
(11, 98000.00, '2021-07-15', '2023-07-14'),
(11, 112000.00, '2023-07-15', NULL),

-- Zack Taylor (Junior Engineer)
(12, 88000.00, '2023-01-09', NULL),

-- Amina Diallo (QA Lead)
(13, 110000.00, '2020-11-15', '2022-11-14'),
(13, 128000.00, '2022-11-15', NULL),

-- Benjamin Hayes (Director Product)
(14, 145000.00, '2019-05-01', '2021-04-30'),
(14, 158000.00, '2021-05-01', '2023-04-30'),
(14, 168000.00, '2023-05-01', NULL),

-- Priya Sharma (Senior TPM)
(15, 125000.00, '2020-08-15', '2022-08-14'),
(15, 142000.00, '2022-08-15', NULL),

-- Oliver Bennett (UX Lead)
(16, 110000.00, '2021-02-01', '2023-01-31'),
(16, 125000.00, '2023-02-01', NULL),

-- Hannah Abbott (Data Analyst)
(17, 88000.00, '2022-04-18', '2023-12-31'),
(17, 98000.00, '2024-01-01', NULL),

-- Gregory House (Enterprise Sales Director)
(18, 135000.00, '2019-09-15', '2022-03-31'),
(18, 155000.00, '2022-04-01', NULL),

-- Jessica Pearson (Senior AE)
(19, 105000.00, '2020-03-01', '2022-09-30'),
(19, 125000.00, '2022-10-01', NULL),

-- Harvey Specter (Enterprise AE)
(20, 105000.00, '2021-01-15', '2023-06-30'),
(20, 120000.00, '2023-07-01', NULL),

-- Rachel Zane (SDR Lead)
(21, 72000.00, '2021-09-01', '2023-08-31'),
(21, 82000.00, '2023-09-01', NULL),

-- Michael Ross (SDR)
(22, 65000.00, '2022-10-10', NULL),

-- Sarah Connor (Marketing Director)
(23, 128000.00, '2019-10-01', '2022-04-30'),
(23, 148000.00, '2022-05-01', NULL),

-- Daniel LaRusso (Content Specialist)
(24, 75000.00, '2021-06-15', '2023-06-14'),
(24, 85000.00, '2023-06-15', NULL),

-- Samantha Jones (Brand Manager)
(25, 92000.00, '2020-04-01', '2022-08-31'),
(25, 105000.00, '2022-09-01', NULL),

-- Julian Assange (Controller)
(26, 120000.00, '2019-12-01', '2022-05-31'),
(26, 135000.00, '2022-06-01', NULL),

-- Grace Hopper (Staff Accountant)
(27, 78000.00, '2021-05-15', '2023-05-14'),
(27, 88000.00, '2023-05-15', NULL),

-- Alan Turing (Senior Financial Analyst)
(28, 102000.00, '2020-07-01', '2022-12-31'),
(28, 115000.00, '2023-01-01', NULL),

-- Evelyn Cross (Head of People)
(29, 128000.00, '2019-02-01', '2022-01-31'),
(29, 145000.00, '2022-02-01', NULL),

-- Nathan Drake (Lead Recruiter)
(30, 85000.00, '2020-06-15', '2023-01-14'),
(30, 98000.00, '2023-01-15', NULL),

-- Lara Croft (HRBP)
(31, 80000.00, '2021-11-01', '2023-10-31'),
(31, 89000.00, '2023-11-01', NULL),

-- Carlos Mendoza (Support Lead)
(32, 78000.00, '2020-01-15', '2022-06-30'),
(32, 88000.00, '2022-07-01', NULL),

-- Fatima Al-Mansoor (Senior Support)
(33, 64000.00, '2021-04-10', '2023-03-31'),
(33, 74000.00, '2023-04-01', NULL),

-- Derek Zoolander (Support Specialist)
(34, 62000.00, '2022-08-01', NULL),

-- Eleanor Vance (Chief Legal Counsel)
(35, 165000.00, '2019-01-05', '2021-12-31'),
(35, 185000.00, '2022-01-01', NULL);
