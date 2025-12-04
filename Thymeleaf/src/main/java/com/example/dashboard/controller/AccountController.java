package com.example.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pak.model.User;
import com.example.pak.service.UserService;

@Controller
public class AccountController {
    private final UserService userService;
    
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        model.addAttribute("title", "Account Management");
        model.addAttribute("accounts", userService.getAllUsers());
        return "accounts/list";
    }

    @GetMapping("/accounts/new")
    public String showAddForm(Model model) {
        model.addAttribute("title", "Add New Account");
        model.addAttribute("account", new User("", "", "Ahli"));
        model.addAttribute("isNew", true);
        return "accounts/form";
    }

    @GetMapping("/accounts/edit")
    public String showEditForm(@RequestParam String username, Model model) {
        var user = userService.findByUsername(username);
        if (user.isEmpty()) {
            model.addAttribute("message", "Account not found");
            model.addAttribute("error", true);
            return "redirect:/accounts";
        }
        model.addAttribute("title", "Edit Account");
        model.addAttribute("account", user.get());
        model.addAttribute("isNew", false);
        return "accounts/form";
    }

    @PostMapping("/accounts/save")
    public String saveAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String oldUsername,
            Model model,
            RedirectAttributes redirectAttrs) {
        
        try {
            if (oldUsername != null && !oldUsername.isEmpty() && !oldUsername.equals(username)) {
                // Edit existing account — check if new username is available
                if (userService.findByUsername(username).isPresent()) {
                    model.addAttribute("message", "Username already exists");
                    model.addAttribute("error", true);
                    model.addAttribute("account", new User(username, password, role));
                    model.addAttribute("isNew", false);
                    return "accounts/form";
                }
                userService.updateUser(oldUsername, username, password, role);
                redirectAttrs.addFlashAttribute("message", "Account updated successfully");
            } else {
                // Add new account
                if (userService.findByUsername(username).isPresent()) {
                    model.addAttribute("message", "Username already exists");
                    model.addAttribute("error", true);
                    model.addAttribute("account", new User(username, password, role));
                    model.addAttribute("isNew", true);
                    return "accounts/form";
                }
                userService.createUser(username, password, role);
                redirectAttrs.addFlashAttribute("message", "Account created successfully");
            }
            redirectAttrs.addFlashAttribute("error", false);
            return "redirect:/accounts";
        } catch (Exception e) {
            model.addAttribute("message", "Error saving account: " + e.getMessage());
            model.addAttribute("error", true);
            return "accounts/form";
        }
    }

    @PostMapping("/accounts/delete")
    public String deleteAccount(@RequestParam String username, RedirectAttributes redirectAttrs) {
        try {
            userService.deleteUser(username);
            redirectAttrs.addFlashAttribute("message", "Account deleted successfully");
            redirectAttrs.addFlashAttribute("error", false);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error deleting account: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/accounts";
    }
}
