-- Add the employee signature step for contracts.
-- Run this on an existing database before using the new signing flow.

ALTER TABLE Contract
    MODIFY Status ENUM(
        'Draft',
        'Pending_Approval',
        'Pending_Signature',
        'Approved',
        'Rejected',
        'Active',
        'Expired'
    ) NOT NULL DEFAULT 'Draft'
    COMMENT 'Contract status: Draft, Pending_Approval, Pending_Signature, Approved, Rejected, Active, Expired';

ALTER TABLE Contract
    ADD COLUMN SignedAt DATETIME NULL AFTER Status,
    ADD COLUMN SignedBy INT NULL AFTER SignedAt;
