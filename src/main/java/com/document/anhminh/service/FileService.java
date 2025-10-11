package com.document.anhminh.service;

import com.document.anhminh.DTO.request.CreateFileLinkRequest;
import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.repository.FileRepository;
import com.document.anhminh.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    private final Path fileStorageLocation;

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    public FileService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục để lưu trữ file.", ex);
        }
    }

    /**
     * Lưu file tải lên
     */
    public FileEntity storeFile(MultipartFile file, Integer folderId) {
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Tên file chứa ký tự không hợp lệ!");
            }

            // Đường dẫn đầy đủ tới file
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            // Copy file vào thư mục lưu trữ (thay thế nếu đã tồn tại)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Lưu thông tin file vào CSDL
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFolder(folder);
            fileEntity.setName(fileName);
            fileEntity.setType(file.getContentType());
            fileEntity.setSize((int) file.getSize());
            fileEntity.setLink(targetLocation.toString()); // Lưu đường dẫn

            return fileRepository.save(fileEntity);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + fileName, ex);
        }
    }

    /**
     * Tạo một bản ghi file mới từ link được cung cấp
     */
    public FileEntity createFileFromLink(CreateFileLinkRequest request) {
        // 1. Tìm folder cha
        FolderEntity folder = folderRepository.findById(request.getFolderId())
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));

        // 2. Tạo đối tượng FileEntity mới
        FileEntity newFile = new FileEntity();
        newFile.setFolder(folder);
        newFile.setName(request.getName());
        newFile.setType(request.getType());
        newFile.setSize(request.getSize());
        newFile.setLink(request.getLink()); // Lấy link trực tiếp từ request

        // 3. Lưu vào cơ sở dữ liệu và trả về
        return fileRepository.save(newFile);
    }

    /**
     * Xóa một file
     */
    public String deleteFile(Integer fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại!"));

        try {
            // Xóa file vật lý trên ổ đĩa
            Path filePath = Paths.get(fileEntity.getLink());
            Files.deleteIfExists(filePath);

            // Xóa thông tin file trong CSDL
            fileRepository.delete(fileEntity);

            return "Đã xóa file thành công: " + fileEntity.getName();
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi khi xóa file!", ex);
        }
    }

    /**
     * Đổi tên một file
     * @param fileId ID của file cần đổi tên
     * @param newName Tên mới (không cần bao gồm đuôi file)
     * @return Thông tin file sau khi đã được đổi tên
     */
    public FileEntity renameFile(Integer fileId, String newName) {
        // 1. Tìm file trong CSDL
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại!"));

        try {
            // 2. Lấy đường dẫn cũ và chuẩn bị đường dẫn mới
            Path oldPath = Paths.get(fileEntity.getLink());
            String originalFilename = oldPath.getFileName().toString();

            // Lấy đuôi file cũ (ví dụ: ".txt", ".jpg")
            String fileExtension = "";
            int lastIndex = originalFilename.lastIndexOf('.');
            if (lastIndex >= 0) {
                fileExtension = originalFilename.substring(lastIndex);
            }

            // Tạo tên file mới hoàn chỉnh bằng cách ghép tên mới và đuôi file cũ
            String finalNewName = newName + fileExtension;
            Path newPath = oldPath.resolveSibling(finalNewName);

            // 3. Thực hiện đổi tên file trên ổ đĩa
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Cập nhật lại thông tin trong đối tượng Entity
            fileEntity.setName(finalNewName);
            fileEntity.setLink(newPath.toString());

            // 5. Lưu lại vào CSDL và trả về
            return fileRepository.save(fileEntity);

        } catch (IOException ex) {
            throw new RuntimeException("Lỗi khi đổi tên file: " + fileEntity.getName(), ex);
        }
    }
}