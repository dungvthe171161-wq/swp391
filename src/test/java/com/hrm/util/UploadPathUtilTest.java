package com.hrm.util;

import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test: UploadPathUtil - 100% Branch Coverage")
public class UploadPathUtilTest {

    @Test
    void testResolveCvDirectoryBranches() {
        // Null context
        Path dir1 = UploadPathUtil.resolveCvDirectory(null);
        assertNotNull(dir1);

        // Mock servlet context with real path
        ServletContext ctx = mock(ServletContext.class);
        when(ctx.getRealPath("/")).thenReturn(System.getProperty("user.dir"));
        Path dir2 = UploadPathUtil.resolveCvDirectory(ctx);
        assertNotNull(dir2);
    }

    @Test
    void testResolveCvFileBranches() {
        // Null / blank / invalid filenames
        assertNull(UploadPathUtil.resolveCvFile(null, null));
        assertNull(UploadPathUtil.resolveCvFile(null, "   "));
        assertNull(UploadPathUtil.resolveCvFile(null, "../malicious.pdf"));
        assertNull(UploadPathUtil.resolveCvFile(null, "script.exe"));
        assertNull(UploadPathUtil.resolveCvFile(null, "file.txt"));

        // Valid filenames
        Path file1 = UploadPathUtil.resolveCvFile(null, "candidate_cv.pdf");
        assertNotNull(file1);
        assertTrue(file1.toString().endsWith("candidate_cv.pdf"));

        Path file2 = UploadPathUtil.resolveCvFile(null, "my_doc.DOCX");
        assertNotNull(file2);

        Path file3 = UploadPathUtil.resolveCvFile(null, "resume.doc");
        assertNotNull(file3);
    }
}
