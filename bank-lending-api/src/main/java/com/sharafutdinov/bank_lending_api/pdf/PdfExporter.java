package com.sharafutdinov.bank_lending_api.pdf;

import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.ExportException;

@Service
@RequiredArgsConstructor
public class PdfExporter {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String REPORT_DIR = "reports/";

    public String uploadFile(MultipartFile file, Long userId) throws ExportException {

        int k = 1;

        if (file.isEmpty()) {
            throw new ResourceNotFoundException("Файл не был передан");
        }

        final String userPath = "/" + userId + "/";

        Path absolutePath = Paths.get(UPLOAD_DIR).toAbsolutePath();

        try {
            File uploadDir = new File(absolutePath + userPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(UPLOAD_DIR + userPath + k + "_" + file.getOriginalFilename());
            while (Files.exists(path)) {
                k++;
                path = Paths.get(UPLOAD_DIR + userPath + k + "_" + file.getOriginalFilename());
            }

            Files.copy(file.getInputStream(), path);

            return path.toString();
        } catch (IOException e) {
            throw new ExportException("Не удалось получить файл");
        }
    }

    public String getUploadFilepath(Long userId) {
        Path absolutePath = Paths.get(UPLOAD_DIR).toAbsolutePath();
        final String userPath = "/" + userId + "/";

        Path path = Paths.get(absolutePath + userPath);

        return path.toString();
    }

    public String getReportFilepath(Long userId) {
        Path absolutePath = Paths.get(REPORT_DIR).toAbsolutePath();
        final String userPath = "/" + userId + "/";

        File uploadDir = new File(absolutePath + userPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Path path = Paths.get(absolutePath + userPath);

        return path.toString();
    }
}
