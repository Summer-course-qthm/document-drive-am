package com.document.anhminh.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveService.class);

    @Value("${gdrive.service-account-key-path}")
    private Resource serviceAccountKey;

    @Value("${gdrive.parent-folder-id}")
    private String parentFolderId;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Hàm để khởi tạo Drive service
    private Drive getDriveService() throws IOException {
        try {
            if (serviceAccountKey == null || !serviceAccountKey.exists()) {
                throw new IOException("Service account key file không tồn tại hoặc không thể đọc được");
            }
            
            GoogleCredential credential = GoogleCredential.fromStream(serviceAccountKey.getInputStream())
                    .createScoped(Collections.singleton(DriveScopes.DRIVE));
            
            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("Document Management App")
                    .build();
        } catch (IOException e) {
            logger.error("Lỗi khi khởi tạo Google Drive service: {}", e.getMessage(), e);
            throw new IOException("Không thể kết nối đến Google Drive: " + e.getMessage(), e);
        }
    }

    // Hàm upload file
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        if (multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }
        
        try {
            Drive driveService = getDriveService();
            File fileMetadata = new File();
            fileMetadata.setName(multipartFile.getOriginalFilename());
            
            if (parentFolderId != null && !parentFolderId.trim().isEmpty()) {
                fileMetadata.setParents(Collections.singletonList(parentFolderId));
            }

            String contentType = multipartFile.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            InputStreamContent mediaContent = new InputStreamContent(
                    contentType,
                    multipartFile.getInputStream()
            );

            File file = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .setSupportsAllDrives(true)
                    .execute();
            
            if (file.getId() == null || file.getId().trim().isEmpty()) {
                throw new IOException("Không thể lấy được File ID từ Google Drive sau khi upload");
            }
            
            logger.info("Upload file thành công: {} với ID: {}", multipartFile.getOriginalFilename(), file.getId());
            return file.getId();
        } catch (IOException e) {
            logger.error("Lỗi khi upload file {}: {}", multipartFile.getOriginalFilename(), e.getMessage(), e);
            throw new IOException("Lỗi khi upload file lên Google Drive: " + e.getMessage(), e);
        }
    }

    // Hàm xóa file
    public void deleteFile(String fileId) throws IOException {
        if (fileId == null || fileId.trim().isEmpty()) {
            throw new IllegalArgumentException("File ID không được để trống");
        }
        
        try {
            Drive driveService = getDriveService();
            driveService.files().delete(fileId)
                    .setSupportsAllDrives(true)
                    .execute();
            logger.info("Xóa file thành công với ID: {}", fileId);
        } catch (IOException e) {
            logger.error("Lỗi khi xóa file với ID {}: {}", fileId, e.getMessage(), e);
            throw new IOException("Lỗi khi xóa file trên Google Drive: " + e.getMessage(), e);
        }
    }

    // Hàm download file
    public void downloadFile(String fileId, OutputStream outputStream) throws IOException {
        if (fileId == null || fileId.trim().isEmpty()) {
            throw new IllegalArgumentException("File ID không được để trống");
        }
        
        if (outputStream == null) {
            throw new IllegalArgumentException("OutputStream không được null");
        }
        
        try {
            Drive driveService = getDriveService();
            driveService.files().get(fileId)
                    .setSupportsAllDrives(true)
                    .executeMediaAndDownloadTo(outputStream);
            logger.info("Download file thành công với ID: {}", fileId);
        } catch (IOException e) {
            logger.error("Lỗi khi download file với ID {}: {}", fileId, e.getMessage(), e);
            throw new IOException("Lỗi khi download file từ Google Drive: " + e.getMessage(), e);
        }
    }
}