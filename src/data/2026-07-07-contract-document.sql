-- Store the readable contract document separately from Contract status/data.

CREATE TABLE IF NOT EXISTS ContractDocument (
    DocumentID INT AUTO_INCREMENT PRIMARY KEY,
    ContractID INT NOT NULL,
    Title VARCHAR(255) NOT NULL DEFAULT 'Van ban hop dong',
    Content TEXT NOT NULL,
    FileName VARCHAR(255) NULL,
    ContentType VARCHAR(100) NULL,
    FileData MEDIUMBLOB NULL,
    FileSize BIGINT NULL,
    VersionNo INT NOT NULL DEFAULT 1,
    CreatedBy INT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contract_document_contract
        FOREIGN KEY (ContractID) REFERENCES Contract(ContractID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
