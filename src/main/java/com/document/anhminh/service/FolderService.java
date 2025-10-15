package com.document.anhminh.service;

import com.document.anhminh.DTO.request.FolderRequest;
import com.document.anhminh.entity.CollectionEntity;
import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.repository.CollectionRepository;
import com.document.anhminh.repository.FileRepository;
import com.document.anhminh.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    // TODO: Sau này sẽ cần FileRepository để xóa các file con khi xóa folder

    /**
     * Tạo một thư mục mới trong một Collection
     */
    public FolderEntity createFolder(FolderRequest request) {
        // Kiểm tra Collection có tồn tại không
        CollectionEntity collection = collectionRepository.findById(request.getCollectionId().intValue())
                .orElseThrow(() -> new RuntimeException("Collection không tồn tại!"));

        // Kiểm tra tên thư mục đã tồn tại trong Collection này chưa
        folderRepository.findByNameAndCollectionId(request.getNameFolder(), collection.getId())
                .ifPresent(folder -> {
                    throw new RuntimeException("Tên thư mục đã tồn tại trong collection này!");
                });

        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(request.getNameFolder());
        newFolder.setCollection(collection);

        return folderRepository.save(newFolder);
    }

    /**
     * Đổi tên một thư mục
     */
    public FolderEntity renameFolder(Integer folderId, String newName) {
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));

        // Kiểm tra tên mới có trùng với thư mục khác trong cùng collection không
        folderRepository.findByNameAndCollectionId(newName, folder.getCollection().getId())
                .ifPresent(f -> {
                    throw new RuntimeException("Tên thư mục đã tồn tại trong collection này!");
                });

        folder.setName(newName);
        return folderRepository.save(folder);
    }

    /**
     * Xóa một thư mục và tất cả các file con bên trong
     */
    // Đảm bảo tất cả các thao tác xóa đều thành công, nếu có lỗi sẽ rollback
    public String deleteFolder(Integer folderId) {
        // 1. Kiểm tra xem thư mục có tồn tại không
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));

        // 2. Tìm tất cả các file nằm trong thư mục này
        List<FileEntity> filesInFolder = fileRepository.findByFolderId(folderId);

        // 3. Xóa từng file (bao gồm cả file vật lý và bản ghi trong CSDL)
        // Bằng cách gọi lại hàm deleteFile đã có sẵn trong FileService
        for (FileEntity file : filesInFolder) {
            fileService.deleteFile(file.getId());
        }

        // 4. Sau khi tất cả các file con đã được xóa, tiến hành xóa thư mục
        folderRepository.delete(folder);

        return "Đã xóa thành công thư mục '" + folder.getName() + "' và " + filesInFolder.size() + " file bên trong.";
    }
}