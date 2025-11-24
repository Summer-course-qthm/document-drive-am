package com.document.anhminh.service;

import com.document.anhminh.DTO.request.CreateFileLinkRequest;
import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.repository.FileRepository;
import com.document.anhminh.repository.FolderRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private GoogleDriveService googleDriveService; // <-- Dùng service mới

    // Lớp nội bộ để chứa dữ liệu tải về
    @Data
    @AllArgsConstructor
    public static class FileDownloadData {
        private Resource resource;
        private String filename;
        private String contentType;
    }

    /**
     * Tải file lên Google Drive và lưu thông tin vào CSDL.
     */
    public FileEntity storeFile(MultipartFile file, Integer folderId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống!");
        }
        
        if (folderId == null) {
            throw new RuntimeException("Folder ID không được để trống!");
        }
        
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));
        
        try {
            // 1. Tải file lên Google Drive và nhận về File ID
            String fileId = googleDriveService.uploadFile(file);
            
            if (fileId == null || fileId.trim().isEmpty()) {
                throw new RuntimeException("Không thể lấy được File ID từ Google Drive sau khi upload");
            }

            // 2. Lưu thông tin vào CSDL
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFolder(folder);
            fileEntity.setName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "untitled");
            fileEntity.setType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
            fileEntity.setSize((int) file.getSize());
            fileEntity.setLink(fileId); // <-- Quan trọng: Lưu Google Drive File ID vào trường link

            return fileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file lên Google Drive: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi không mong đợi khi upload file: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa file trên Google Drive và trong CSDL.
     */
    public String deleteFile(Integer fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại!"));
        try {
            // 1. Xóa file trên Google Drive bằng ID đã lưu
            googleDriveService.deleteFile(fileEntity.getLink());

            // 2. Xóa bản ghi trong CSDL
            fileRepository.delete(fileEntity);

            return "Đã xóa file thành công: " + fileEntity.getName();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa file trên Google Drive!", e);
        }
    }

    /**
     * Tải file từ Google Drive.
     */
    public FileDownloadData loadFileAsResource(Integer fileId) {
        if (fileId == null) {
            throw new RuntimeException("File ID không được để trống!");
        }
        
        try {
            FileEntity fileEntity = fileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File không tồn tại!"));

            if (fileEntity.getLink() == null || fileEntity.getLink().trim().isEmpty()) {
                throw new RuntimeException("File không có Google Drive ID!");
            }

            // Dùng ByteArrayOutputStream để hứng dữ liệu từ Google Drive
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            googleDriveService.downloadFile(fileEntity.getLink(), outputStream);
            
            byte[] fileData = outputStream.toByteArray();
            if (fileData.length == 0) {
                throw new RuntimeException("File tải về từ Google Drive rỗng!");
            }

            // Gói dữ liệu thành Resource để trả về
            Resource resource = new ByteArrayResource(fileData);

            return new FileDownloadData(
                    resource, 
                    fileEntity.getName() != null ? fileEntity.getName() : "file",
                    fileEntity.getType() != null ? fileEntity.getType() : "application/octet-stream"
            );
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file từ Google Drive: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi không mong đợi khi download file: " + e.getMessage(), e);
        }
    }

    // Chức năng này vẫn hoạt động như cũ vì nó không xử lý file vật lý
    public FileEntity createFileFromLink(CreateFileLinkRequest request) {
        FolderEntity folder = folderRepository.findById(request.getFolderId())
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));
        FileEntity newFile = new FileEntity();
        newFile.setFolder(folder);
        newFile.setName(request.getName());
        newFile.setType(request.getType());
        newFile.setSize(request.getSize());
        newFile.setLink(request.getLink());
        return fileRepository.save(newFile);
    }

    // Chức năng đổi tên cần logic riêng để tương tác với Google Drive API
    public FileEntity renameFile(Integer fileId, String newName) {
        throw new UnsupportedOperationException("Chức năng đổi tên file trên Google Drive chưa được cài đặt.");
    }

    public List<FileEntity> getFilesByFolder(Integer folderId) {
        if (!folderRepository.existsById(folderId)) {
            throw new RuntimeException("Folder không tồn tại!");
        }
        return fileRepository.findByFolderId(folderId);
    }

    public FileEntity getFile(Integer fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại!"));
    }
}