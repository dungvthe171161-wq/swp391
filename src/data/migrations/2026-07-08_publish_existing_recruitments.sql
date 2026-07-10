-- Make existing HR-created recruitment posts visible to candidates.
-- Current public apply flow only accepts Recruitment.Status = 'Applied'.

UPDATE Recruitment
SET Status = 'Applied'
WHERE Status = 'New';
