package com.document.anhminh.service;

import com.document.anhminh.DTO.request.FolderRequest;
import com.document.anhminh.entity.CollectionEntity;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.repository.CollectionRepository;
import com.document.anhminh.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private CollectionRepository collectionRepository;
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
     * Xóa một thư mục
     * Chú ý: Cần thêm logic để xóa các file con bên trong
     */
    public String deleteFolder(Integer folderId) {
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Thư mục không tồn tại!"));

        // TODO: Thêm logic xóa file trong thư mục trên ổ đĩa và trong CSDL

        folderRepository.delete(folder);
        return "Đã xóa thành công thư mục: " + folder.getName();
    }
}