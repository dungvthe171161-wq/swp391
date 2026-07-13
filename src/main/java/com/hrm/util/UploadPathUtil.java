package com.hrm.util;

import jakarta.servlet.ServletContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class UploadPathUtil {

    private static final String CV_UPLOAD_PROPERTY = "hrms.cv.upload.dir";
    private static final String CV_UPLOAD_ENV = "HRMS_CV_UPLOAD_DIR";

    private UploadPathUtil() {
    }

    public static Path resolveCvDirectory(ServletContext servletContext) {
        String configuredPath = firstNonBlank(
                System.getProperty(CV_UPLOAD_PROPERTY),
                System.getenv(CV_UPLOAD_ENV));
        if (configuredPath != null) {
            return Paths.get(configuredPath).toAbsolutePath().normalize();
        }

        Path projectRoot = findProjectRoot(getRealPath(servletContext, "/"));
        if (projectRoot == null) {
            projectRoot = findProjectRoot(Paths.get(System.getProperty("user.dir", ".")));
        }
        if (projectRoot != null) {
            return projectRoot.resolve("src/main/webapp/Upload/cvs").normalize();
        }

        Path deployedDirectory = getRealPath(servletContext, "/Upload/cvs");
        if (deployedDirectory != null) {
            return deployedDirectory.toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.home"), "hrms", "Upload", "cvs")
                .toAbsolutePath()
                .normalize();
    }

    public static Path resolveCvFile(ServletContext servletContext, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        String safeName;
        try {
            safeName = Paths.get(fileName).getFileName().toString();
        } catch (RuntimeException ex) {
            return null;
        }
        if (!safeName.equals(fileName) || !safeName.toLowerCase().matches(".+\\.(pdf|doc|docx)")) {
            return null;
        }
        Path directory = resolveCvDirectory(servletContext);
        Path file = directory.resolve(safeName).normalize();
        return file.startsWith(directory) ? file : null;
    }

    private static Path findProjectRoot(Path start) {
        if (start == null) {
            return null;
        }
        Path current = start.toAbsolutePath().normalize();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("pom.xml"))
                    && Files.isDirectory(current.resolve("src/main/webapp"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    private static Path getRealPath(ServletContext servletContext, String path) {
        if (servletContext == null) {
            return null;
        }
        String realPath = servletContext.getRealPath(path);
        return realPath == null || realPath.isBlank() ? null : Paths.get(realPath);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}