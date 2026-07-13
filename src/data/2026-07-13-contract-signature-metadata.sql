-- Store employee drawn signatures and signing metadata for online contracts.
-- Run this after 2026-07-07-contract-employee-signature.sql on existing databases.

ALTER TABLE Contract
    ADD COLUMN EmployeeSignaturePath VARCHAR(500) NULL AFTER SignedBy,
    ADD COLUMN SignatureHash CHAR(64) NULL AFTER EmployeeSignaturePath,
    ADD COLUMN SignIp VARCHAR(45) NULL AFTER SignatureHash,
    ADD COLUMN SignUserAgent VARCHAR(255) NULL AFTER SignIp,
    ADD COLUMN ContractContentHash CHAR(64) NULL AFTER SignUserAgent;
