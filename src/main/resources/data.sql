-- Make gpa column nullable if it still exists (idempotent — spring.sql.init.continue-on-error=true handles failures)
ALTER TABLE student_profiles ALTER COLUMN gpa DROP NOT NULL;

-- Patch any skills that were created without a category (null or empty string)
UPDATE skills SET category = 'Other' WHERE category IS NULL OR category = '';
