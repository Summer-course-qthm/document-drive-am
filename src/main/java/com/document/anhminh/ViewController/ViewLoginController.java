package com.document.anhminh.ViewController;

import com.document.anhminh.DTO.request.LoginRequest;
import com.document.anhminh.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewLoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @ModelAttribute("loginRequest") LoginRequest request,
            HttpServletResponse response
    ) {
        try {
            String token = userService.login(request);

            // SỬA LẠI TÊN COOKIE CHO KHỚP VỚI FILTER
            Cookie jwtCookie = new Cookie("jwt-token", token); // <-- SỬA Ở ĐÂY
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60 * 24);

            response.addCookie(jwtCookie);
            return "redirect:/";

        } catch (Exception e) {
            return "redirect:/login?error=true";
        }
    }
}