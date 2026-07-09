-- Add optional uploaded file storage for ContractDocument.
-- Run this only if ContractDocument was created before the file columns were added.

ALTER TABLE ContractDocument ADD COLUMN FileName VARCHAR(255) NULL;
ALTER TABLE ContractDocument ADD COLUMN ContentType VARCHAR(100) NULL;
ALTER TABLE ContractDocument ADD COLUMN FileData MEDIUMBLOB NULL;
ALTER TABLE ContractDocument ADD COLUMN FileSize BIGINT NULL;
