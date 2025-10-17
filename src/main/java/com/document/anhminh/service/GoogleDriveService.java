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

    @Value("${gdrive.service-account-key-path}")
    private Resource serviceAccountKey;

    @Value("${gdrive.parent-folder-id}")
    private String parentFolderId;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Hàm để khởi tạo Drive service
    private Drive getDriveService() throws IOException {
        GoogleCredential credential = GoogleCredential.fromStream(serviceAccountKey.getInputStream())
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Document Management App")
                .build();
    }

    // Hàm upload file
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        Drive driveService = getDriveService();
        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(parentFolderId));

        InputStreamContent mediaContent = new InputStreamContent(
                multipartFile.getContentType(),
                multipartFile.getInputStream()
        );

        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .setSupportsAllDrives(true)
                .execute();
        return file.getId();
    }

    // Hàm xóa file
    public void deleteFile(String fileId) throws IOException {
        Drive driveService = getDriveService();
        driveService.files().delete(fileId)
                .setSupportsAllDrives(true) // <-- THÊM DÒNG NÀY
                .execute();
    }

    // Hàm download file
    public void downloadFile(String fileId, OutputStream outputStream) throws IOException {
        Drive driveService = getDriveService();
        driveService.files().get(fileId)
                .setSupportsAllDrives(true) // <-- THÊM DÒNG NÀY
                .executeMediaAndDownloadTo(outputStream);
    }
}