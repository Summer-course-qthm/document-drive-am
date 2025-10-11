package com.document.anhminh.service;

import com.document.anhminh.DTO.request.CollectionRequest;
import com.document.anhminh.entity.CollectionEntity;
import com.document.anhminh.entity.UserEntity;
import com.document.anhminh.repository.CollectionRepository;
import com.document.anhminh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;

    public CollectionEntity createCollection(CollectionRequest request) {
        UserEntity user = userRepository.findById(request.getUser_Id())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        CollectionEntity collection = new CollectionEntity();
        collection.setName(request.getName());
        collection.setUserid(user);

        return collectionRepository.save(collection);
    }
}
