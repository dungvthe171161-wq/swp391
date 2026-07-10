package com.hrm.dao;

import com.hrm.model.entity.ContractDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ContractDocumentDAO {

    public boolean create(ContractDocument document) {
        String sql = """
            INSERT INTO ContractDocument
                (ContractID, Title, Content, FileName, ContentType, FileData, FileSize, VersionNo, CreatedBy)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, document.getContractId());
            ps.setString(2, cleanTitle(document.getTitle()));
            ps.setString(3, cleanContent(document.getContent(), document.getFileName()));
            bindFile(ps, document, 4);
            ps.setInt(8, document.getVersionNo() > 0 ? document.getVersionNo() : 1);
            if (document.getCreatedBy() != null && document.getCreatedBy() > 0) {
                ps.setInt(9, document.getCreatedBy());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean replaceLatest(int contractId, String title, String content, Integer createdBy) {
        ContractDocument document = new ContractDocument();
        document.setContractId(contractId);
        document.setTitle(title);
        document.setContent(content);
        document.setCreatedBy(createdBy);
        return replaceLatest(document);
    }

    public boolean replaceLatest(ContractDocument document) {
        ContractDocument latest = getLatestByContractId(document.getContractId());
        if (latest == null) {
            document.setVersionNo(1);
            return create(document);
        }

        boolean hasNewFile = hasFile(document);
        String sql = hasNewFile
                ? """
                    UPDATE ContractDocument
                    SET Title = ?, Content = ?, FileName = ?, ContentType = ?, FileData = ?, FileSize = ?,
                        VersionNo = VersionNo + 1, CreatedBy = ?, CreatedAt = NOW()
                    WHERE DocumentID = ?
                """
                : """
                    UPDATE ContractDocument
                    SET Title = ?, Content = ?, VersionNo = VersionNo + 1, CreatedBy = ?, CreatedAt = NOW()
                    WHERE DocumentID = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cleanTitle(document.getTitle()));
            ps.setString(2, cleanContent(document.getContent(), hasNewFile ? document.getFileName() : latest.getFileName()));
            if (hasNewFile) {
                bindFile(ps, document, 3);
                if (document.getCreatedBy() != null && document.getCreatedBy() > 0) {
                    ps.setInt(7, document.getCreatedBy());
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }
                ps.setInt(8, latest.getDocumentId());
            } else {
                if (document.getCreatedBy() != null && document.getCreatedBy() > 0) {
                    ps.setInt(3, document.getCreatedBy());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setInt(4, latest.getDocumentId());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public ContractDocument getLatestByContractId(int contractId) {
        String sql = """
            SELECT DocumentID, ContractID, Title, Content, FileName, ContentType, FileData, FileSize,
                   VersionNo, CreatedBy, CreatedAt
            FROM ContractDocument
            WHERE ContractID = ?
            ORDER BY VersionNo DESC, DocumentID DESC
            LIMIT 1
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDocument(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean hasReadableDocument(int contractId) {
        ContractDocument document = getLatestByContractId(contractId);
        return document != null
                && ((document.getContent() != null && !document.getContent().trim().isEmpty())
                    || hasFile(document));
    }

    private ContractDocument mapDocument(ResultSet rs) throws SQLException {
        ContractDocument document = new ContractDocument();
        document.setDocumentId(rs.getInt("DocumentID"));
        document.setContractId(rs.getInt("ContractID"));
        document.setTitle(rs.getString("Title"));
        document.setContent(rs.getString("Content"));
        document.setFileName(rs.getString("FileName"));
        document.setContentType(rs.getString("ContentType"));
        byte[] fileData = rs.getBytes("FileData");
        document.setFileData(fileData);
        Object fileSize = rs.getObject("FileSize");
        document.setFileSize(fileSize instanceof Number ? ((Number) fileSize).longValue() : null);
        document.setVersionNo(rs.getInt("VersionNo"));
        Object createdBy = rs.getObject("CreatedBy");
        document.setCreatedBy(createdBy instanceof Number ? ((Number) createdBy).intValue() : null);
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        document.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return document;
    }

    private String cleanTitle(String title) {
        return title == null || title.trim().isEmpty() ? "Van ban hop dong" : title.trim();
    }

    private String cleanContent(String content, String fileName) {
        if (content != null && !content.trim().isEmpty()) {
            return content.trim();
        }
        if (fileName != null && !fileName.trim().isEmpty()) {
            return "Van ban hop dong duoc dinh kem trong tep: " + fileName.trim();
        }
        return "";
    }

    private void bindFile(PreparedStatement ps, ContractDocument document, int startIndex) throws SQLException {
        ps.setString(startIndex, document.getFileName());
        ps.setString(startIndex + 1, document.getContentType());
        if (hasFile(document)) {
            ps.setBytes(startIndex + 2, document.getFileData());
            ps.setLong(startIndex + 3, document.getFileSize() != null ? document.getFileSize() : document.getFileData().length);
        } else {
            ps.setNull(startIndex + 2, java.sql.Types.BLOB);
            ps.setNull(startIndex + 3, java.sql.Types.BIGINT);
        }
    }

    private boolean hasFile(ContractDocument document) {
        return document != null
                && document.getFileData() != null
                && document.getFileData().length > 0
                && document.getFileName() != null
                && !document.getFileName().trim().isEmpty();
    }
}
