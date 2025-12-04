package com.example.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MembersController {
    // In-memory storage for members (using a mutable list)
    private static final java.util.List<java.util.Map<String, Object>> memberStore = 
        new java.util.ArrayList<>();
    
    static {
        // Initialize with mutable HashMaps
        java.util.Map<String, Object> m1 = new java.util.HashMap<>();
        m1.put("id", 1);
        m1.put("name", "Alice");
        m1.put("role", "Member");
        m1.put("email", "alice@example.com");
        m1.put("phone", "012-3456789");
        memberStore.add(m1);
        
        java.util.Map<String, Object> m2 = new java.util.HashMap<>();
        m2.put("id", 2);
        m2.put("name", "Bob");
        m2.put("role", "Admin");
        m2.put("email", "bob@example.com");
        m2.put("phone", "012-9876543");
        memberStore.add(m2);
        
        java.util.Map<String, Object> m3 = new java.util.HashMap<>();
        m3.put("id", 3);
        m3.put("name", "Carol");
        m3.put("role", "Member");
        m3.put("email", "carol@example.com");
        m3.put("phone", "012-5555555");
        memberStore.add(m3);
    }

    @GetMapping("/members")
    public String members(Model model){
        model.addAttribute("title", "Member Administration");
        model.addAttribute("members", memberStore);
        return "members";
    }

    @GetMapping("/members/profile")
    public String viewProfile(@RequestParam int id, Model model) {
        java.util.Optional<java.util.Map<String, Object>> member = memberStore.stream()
            .filter(m -> (int) m.get("id") == id)
            .findFirst();
        
        if (member.isEmpty()) {
            return "redirect:/members";
        }
        
        model.addAttribute("title", "Member Profile");
        model.addAttribute("member", member.get());
        return "members/profile";
    }

    @GetMapping("/members/edit")
    public String showEditForm(@RequestParam int id, Model model) {
        java.util.Optional<java.util.Map<String, Object>> member = memberStore.stream()
            .filter(m -> (int) m.get("id") == id)
            .findFirst();
        
        if (member.isEmpty()) {
            return "redirect:/members";
        }
        
        model.addAttribute("title", "Edit Member Profile");
        model.addAttribute("member", member.get());
        return "members/edit";
    }

    @PostMapping("/members/save-profile")
    public String saveProfile(
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            RedirectAttributes redirectAttrs) {
        try {
            java.util.Optional<java.util.Map<String, Object>> member = memberStore.stream()
                .filter(m -> (int) m.get("id") == id)
                .findFirst();
            
            if (member.isPresent()) {
                java.util.Map<String, Object> m = member.get();
                m.put("name", name);
                m.put("email", email);
                m.put("phone", phone);
                
                redirectAttrs.addFlashAttribute("message", "Profile updated successfully");
                redirectAttrs.addFlashAttribute("error", false);
            } else {
                redirectAttrs.addFlashAttribute("message", "Member not found");
                redirectAttrs.addFlashAttribute("error", true);
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error updating profile: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/members/profile?id=" + id;
    }

    @PostMapping("/members/delete")
    public String deleteMember(
            @RequestParam int id,
            RedirectAttributes redirectAttrs) {
        try {
            boolean removed = memberStore.removeIf(m -> (int) m.get("id") == id);
            
            if (removed) {
                redirectAttrs.addFlashAttribute("message", "Member deleted successfully");
                redirectAttrs.addFlashAttribute("error", false);
            } else {
                redirectAttrs.addFlashAttribute("message", "Member not found");
                redirectAttrs.addFlashAttribute("error", true);
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error deleting member: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/members";
    }
}
