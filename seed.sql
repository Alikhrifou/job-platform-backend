-- =====================================================================
--  JobMatch – Seed Data
--  Run once against jobmatch_db.
--  All test-user passwords: "password"
-- =====================================================================

-- ── 1. Skills ─────────────────────────────────────────────────────────
INSERT INTO skills (name, category, description) VALUES
  ('Java',           'Backend',   'Java programming language'),
  ('Spring Boot',    'Backend',   'Java framework for REST APIs'),
  ('Python',         'Backend',   'Python programming language'),
  ('Django',         'Backend',   'Python web framework'),
  ('JavaScript',     'Frontend',  'JavaScript language'),
  ('TypeScript',     'Frontend',  'Typed superset of JavaScript'),
  ('React',          'Frontend',  'React.js UI library'),
  ('Vue.js',         'Frontend',  'Vue.js UI framework'),
  ('SQL',            'Database',  'Relational database querying'),
  ('PostgreSQL',     'Database',  'PostgreSQL RDBMS'),
  ('MongoDB',        'Database',  'NoSQL document database'),
  ('Docker',         'DevOps',    'Container technology'),
  ('Git',            'Tools',     'Version control system'),
  ('REST API',       'Backend',   'RESTful API design'),
  ('Machine Learning','AI',       'ML algorithms and models')
ON CONFLICT (name) DO NOTHING;

-- ── 2. Users ──────────────────────────────────────────────────────────
-- BCrypt hash of "password" (strength 10)
INSERT INTO users (email, password, first_name, last_name, phone_number, role, is_active, created_at, updated_at) VALUES
  ('student@test.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ahmed',  'Benali',  '+213600000001', 'STUDENT',  true, NOW(), NOW()),
  ('techcorp@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Tech',   'Corp',    '+213600000002', 'COMPANY',  true, NOW(), NOW()),
  ('startup@test.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Start',  'Up',      '+213600000003', 'COMPANY',  true, NOW(), NOW()),
  ('admin@test.com',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Super',  'Admin',   '+213600000004', 'ADMIN',    true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- ── 3. Company Profiles ───────────────────────────────────────────────
INSERT INTO company_profiles (user_id, company_name, industry, website, address, city, description, employee_count)
SELECT u.id, 'TechCorp Maroc', 'Technology', 'https://techcorp.dz',
       '15 Rue Didouche Mourad', 'Maroc',
       'Leading tech company in Maroc specializing in cloud solutions and enterprise software.',
       250
FROM users u WHERE u.email = 'techcorp@test.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO company_profiles (user_id, company_name, industry, website, address, city, description, employee_count)
SELECT u.id, 'StartUp Vision', 'FinTech', 'https://startupvision.dz',
       '7 Boulevard Colonel Lotfi', 'Oran',
       'Innovative fintech startup disrupting the payment industry in North Africa.',
       45
FROM users u WHERE u.email = 'startup@test.com'
ON CONFLICT (user_id) DO NOTHING;

-- ── 4. Student Profile ────────────────────────────────────────────────
INSERT INTO student_profiles (user_id, university, major, graduation_date, bio, portfolio_url, gpa)
SELECT u.id,
       'University of Maroc 1',
       'Computer Science',
       '2025-06-30',
       'Passionate about backend development and distributed systems. Looking for internship or junior dev roles.',
       'https://github.com/ahmed-benali',
       3.70
FROM users u WHERE u.email = 'student@test.com'
ON CONFLICT (user_id) DO NOTHING;

-- ── 5. Student Skills ─────────────────────────────────────────────────
INSERT INTO student_skills (student_id, skill_id, level)
SELECT sp.id, s.id, 4
FROM student_profiles sp
JOIN users u ON u.id = sp.user_id
JOIN skills s ON s.name = 'Java'
WHERE u.email = 'student@test.com'
ON CONFLICT (student_id, skill_id) DO NOTHING;

INSERT INTO student_skills (student_id, skill_id, level)
SELECT sp.id, s.id, 3
FROM student_profiles sp
JOIN users u ON u.id = sp.user_id
JOIN skills s ON s.name = 'Spring Boot'
WHERE u.email = 'student@test.com'
ON CONFLICT (student_id, skill_id) DO NOTHING;

INSERT INTO student_skills (student_id, skill_id, level)
SELECT sp.id, s.id, 3
FROM student_profiles sp
JOIN users u ON u.id = sp.user_id
JOIN skills s ON s.name = 'SQL'
WHERE u.email = 'student@test.com'
ON CONFLICT (student_id, skill_id) DO NOTHING;

INSERT INTO student_skills (student_id, skill_id, level)
SELECT sp.id, s.id, 4
FROM student_profiles sp
JOIN users u ON u.id = sp.user_id
JOIN skills s ON s.name = 'React'
WHERE u.email = 'student@test.com'
ON CONFLICT (student_id, skill_id) DO NOTHING;

INSERT INTO student_skills (student_id, skill_id, level)
SELECT sp.id, s.id, 5
FROM student_profiles sp
JOIN users u ON u.id = sp.user_id
JOIN skills s ON s.name = 'Git'
WHERE u.email = 'student@test.com'
ON CONFLICT (student_id, skill_id) DO NOTHING;

INSERT INTO student_skills (student_id, skill_id, level)
SELECT sp.id, s.id, 2
FROM student_profiles sp
JOIN users u ON u.id = sp.user_id
JOIN skills s ON s.name = 'Docker'
WHERE u.email = 'student@test.com'
ON CONFLICT (student_id, skill_id) DO NOTHING;

-- ── 6. Job Offers ─────────────────────────────────────────────────────
-- TechCorp job 1: Full-stack developer
INSERT INTO job_offers (company_id, title, description, location, job_type, salary, is_active, created_at)
SELECT cp.id,
       'Full-Stack Java Developer',
       E'We are looking for an experienced full-stack developer.\n\nResponsibilities:\n- Develop and maintain Java backend services\n- Build React frontends\n- Write clean, testable code\n- Participate in code reviews\n\nRequirements:\n- 2+ years experience with Java and Spring Boot\n- Experience with React\n- Good knowledge of SQL databases',
       'Maroc (Hybrid)',
       'JOB',
       120000.0,
       true,
       NOW()
FROM company_profiles cp
JOIN users u ON u.id = cp.user_id
WHERE u.email = 'techcorp@test.com';

-- TechCorp job 2: Backend internship
INSERT INTO job_offers (company_id, title, description, location, job_type, salary, is_active, created_at)
SELECT cp.id,
       'Backend Developer Intern',
       E'Join our engineering team for a 6-month internship.\n\nYou will:\n- Work on real production code\n- Learn microservices architecture\n- Collaborate with senior engineers\n\nWe are looking for final-year students with a strong grasp of Java and databases.',
       'Maroc (On-site)',
       'INTERNSHIP',
       30000.0,
       true,
       NOW()
FROM company_profiles cp
JOIN users u ON u.id = cp.user_id
WHERE u.email = 'techcorp@test.com';

-- StartUp job: React developer
INSERT INTO job_offers (company_id, title, description, location, job_type, salary, is_active, created_at)
SELECT cp.id,
       'Frontend React Developer',
       E'StartUp Vision is hiring a frontend developer to build our next-gen payment dashboard.\n\nWhat you will do:\n- Build responsive UIs with React & TypeScript\n- Integrate REST APIs\n- Optimize performance\n\nWhat we offer:\n- Competitive salary\n- Flexible hours\n- Stock options',
       'Oran (Remote)',
       'JOB',
       100000.0,
       true,
       NOW()
FROM company_profiles cp
JOIN users u ON u.id = cp.user_id
WHERE u.email = 'startup@test.com';

-- StartUp job: Part-time data analyst
INSERT INTO job_offers (company_id, title, description, location, job_type, salary, is_active, created_at)
SELECT cp.id,
       'Data Analyst (Part-Time)',
       E'We need a part-time data analyst to help us make data-driven decisions.\n\nTasks:\n- Analyze transaction data using SQL and Python\n- Build dashboards\n- Identify trends\n\nFlexible schedule, 20h/week.',
       'Remote',
       'PART_TIME',
       50000.0,
       true,
       NOW()
FROM company_profiles cp
JOIN users u ON u.id = cp.user_id
WHERE u.email = 'startup@test.com';

-- ── 7. Job Skills ─────────────────────────────────────────────────────
-- Full-Stack Java Developer
INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 4
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'Java'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Full-Stack Java Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 3
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'Spring Boot'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Full-Stack Java Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 3
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'React'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Full-Stack Java Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 3
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'SQL'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Full-Stack Java Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

-- Backend Developer Intern
INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 2
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'Java'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Backend Developer Intern'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 2
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'SQL'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Backend Developer Intern'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 1
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'Git'
WHERE u.email = 'techcorp@test.com' AND jo.title = 'Backend Developer Intern'
ON CONFLICT (job_id, skill_id) DO NOTHING;

-- Frontend React Developer
INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 4
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'React'
WHERE u.email = 'startup@test.com' AND jo.title = 'Frontend React Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 4
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'TypeScript'
WHERE u.email = 'startup@test.com' AND jo.title = 'Frontend React Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 2
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'REST API'
WHERE u.email = 'startup@test.com' AND jo.title = 'Frontend React Developer'
ON CONFLICT (job_id, skill_id) DO NOTHING;

-- Data Analyst (Part-Time)
INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 3
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'Python'
WHERE u.email = 'startup@test.com' AND jo.title = 'Data Analyst (Part-Time)'
ON CONFLICT (job_id, skill_id) DO NOTHING;

INSERT INTO job_skills (job_id, skill_id, required_level)
SELECT jo.id, s.id, 3
FROM job_offers jo
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users u ON u.id = cp.user_id
JOIN skills s ON s.name = 'SQL'
WHERE u.email = 'startup@test.com' AND jo.title = 'Data Analyst (Part-Time)'
ON CONFLICT (job_id, skill_id) DO NOTHING;

-- ── 8. Applications ───────────────────────────────────────────────────
-- Student applies to Full-Stack Java Developer (high match)
INSERT INTO applications (student_id, job_id, status, cover_letter, applied_at, match_score)
SELECT sp.id, jo.id, 'PENDING',
       'I am a computer science student with strong Java and React skills. I am very excited about this opportunity at TechCorp Maroc and believe my background aligns well with the role.',
       NOW(),
       87.5
FROM student_profiles sp
JOIN users su ON su.id = sp.user_id
JOIN job_offers jo ON jo.title = 'Full-Stack Java Developer'
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users cu ON cu.id = cp.user_id
WHERE su.email = 'student@test.com' AND cu.email = 'techcorp@test.com';

-- Student applies to Backend Developer Intern (high match)
INSERT INTO applications (student_id, job_id, status, cover_letter, applied_at, match_score)
SELECT sp.id, jo.id, 'SHORTLISTED',
       'As a final-year CS student I am eager to gain industry experience. I have been working with Java and SQL for 2 years and believe this internship is a perfect fit.',
       NOW() - INTERVAL '3 days',
       95.0
FROM student_profiles sp
JOIN users su ON su.id = sp.user_id
JOIN job_offers jo ON jo.title = 'Backend Developer Intern'
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users cu ON cu.id = cp.user_id
WHERE su.email = 'student@test.com' AND cu.email = 'techcorp@test.com';

-- Student applies to Frontend React Developer
INSERT INTO applications (student_id, job_id, status, cover_letter, applied_at, match_score)
SELECT sp.id, jo.id, 'PENDING',
       'I have been building React applications for the past year and recently picked up TypeScript. I am excited about the startup environment and the fintech domain.',
       NOW() - INTERVAL '1 day',
       65.0
FROM student_profiles sp
JOIN users su ON su.id = sp.user_id
JOIN job_offers jo ON jo.title = 'Frontend React Developer'
JOIN company_profiles cp ON cp.id = jo.company_id
JOIN users cu ON cu.id = cp.user_id
WHERE su.email = 'student@test.com' AND cu.email = 'startup@test.com';

-- ── Done ──────────────────────────────────────────────────────────────
SELECT 'Seed complete.' AS result;
