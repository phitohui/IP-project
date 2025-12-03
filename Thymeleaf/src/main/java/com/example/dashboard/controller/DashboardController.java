package com.example.dashboard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/","/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("title", "Admin Dashboard");

        List<Map<String, Object>> cards = List.of(
                Map.of("label", "Users", "value", 1245, "color", "primary"),
                Map.of("label", "Orders", "value", 342, "color", "success"),
                Map.of("label", "Revenue", "value", "$12,430", "color", "info"),
                Map.of("label", "Alerts", "value", 3, "color", "danger")
        );

        model.addAttribute("cards", cards);

        List<Map<String, Object>> recent = List.of(
                Map.of("id", 101, "name", "Alice", "status", "Active"),
                Map.of("id", 102, "name", "Bob", "status", "Pending"),
                Map.of("id", 103, "name", "Carol", "status", "Active")
        );
        model.addAttribute("recent", recent);

        return "dashboard";
    }
}
