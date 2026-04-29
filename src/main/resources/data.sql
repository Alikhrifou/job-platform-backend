-- Make gpa column nullable if it still exists (idempotent — spring.sql.init.continue-on-error=true handles failures)
ALTER TABLE student_profiles ALTER COLUMN gpa DROP NOT NULL;
