package com.document.anhminh.ViewController;

import com.document.anhminh.DTO.request.RegisterRequest;
import com.document.anhminh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewRegisterController {

    @Autowired
    private UserService userService;

    /**
     * 1. Hiển thị trang đăng ký
     * Khi người dùng truy cập GET /register, hàm này sẽ chạy
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("userRequest", new RegisterRequest());
        return "register";
    }

    /**
     * 2. Xử lý dữ liệu từ form đăng ký
     * Khi người dùng nhấn submit (POST /register), hàm này sẽ chạy
     */
    @PostMapping("/register")
    public String handleRegisterForm(@ModelAttribute("userRequest") RegisterRequest user) {
        try {

            userService.register(user);

            return "redirect:/login?register=success";
        } catch (Exception e) {

            return "redirect:/register?error=true";
        }
    }
}