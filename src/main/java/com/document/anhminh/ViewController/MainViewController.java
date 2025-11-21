package com.document.anhminh.ViewController;

import com.document.anhminh.entity.CollectionEntity;
import com.document.anhminh.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class MainViewController {

    @Autowired
    private CollectionService collectionService;

    /**
     * TRANG CHỦ (Homepage)
     * Hiển thị tất cả Collections
     */
    @GetMapping("/")
    public String homePage(Model model) {
        List<CollectionEntity> collections = collectionService.getAllCollections();
        model.addAttribute("danhSachCollection", collections);
        return "index";
    }
}