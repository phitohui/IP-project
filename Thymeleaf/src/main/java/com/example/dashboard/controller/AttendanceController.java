package com.example.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AttendanceController {
    // In-memory storage for attendance records (using a mutable list)
    private static final java.util.List<java.util.Map<String, Object>> attendanceStore = 
        new java.util.ArrayList<>();
    
    static {
        // Initialize with mutable HashMaps
        java.util.Map<String, Object> a1 = new java.util.HashMap<>();
        a1.put("id", 1);
        a1.put("meetingId", 1);
        a1.put("member", "Alice");
        a1.put("status", "Present");
        attendanceStore.add(a1);
        
        java.util.Map<String, Object> a2 = new java.util.HashMap<>();
        a2.put("id", 2);
        a2.put("meetingId", 1);
        a2.put("member", "Bob");
        a2.put("status", "Absent");
        attendanceStore.add(a2);
        
        java.util.Map<String, Object> a3 = new java.util.HashMap<>();
        a3.put("id", 3);
        a3.put("meetingId", 2);
        a3.put("member", "Carol");
        a3.put("status", "Present");
        attendanceStore.add(a3);
    }

    @GetMapping("/attendance")
    public String attendance(Model model){
        model.addAttribute("title", "Attendance Management");
        model.addAttribute("attendanceList", attendanceStore);
        return "attendance";
    }

    @GetMapping("/attendance/new")
    public String showNewAttendanceForm(Model model) {
        model.addAttribute("title", "Record Attendance");
        // Sample members for dropdown
        java.util.List<String> members = java.util.List.of("Alice", "Bob", "Carol", "David", "Eve");
        model.addAttribute("members", members);
        return "attendance/form";
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(
            @RequestParam int meetingId,
            @RequestParam String member,
            @RequestParam String status,
            RedirectAttributes redirectAttrs) {
        try {
            // Validate status
            if (!status.equals("Present") && !status.equals("Absent")) {
                redirectAttrs.addFlashAttribute("message", "Invalid status. Must be Present or Absent");
                redirectAttrs.addFlashAttribute("error", true);
                return "redirect:/attendance";
            }

            // Generate new ID
            int newId = attendanceStore.stream()
                .mapToInt(a -> (int) a.get("id"))
                .max()
                .orElse(0) + 1;

            // Create new attendance record
            java.util.Map<String, Object> newAttendance = new java.util.HashMap<>();
            newAttendance.put("id", newId);
            newAttendance.put("meetingId", meetingId);
            newAttendance.put("member", member);
            newAttendance.put("status", status);

            attendanceStore.add(newAttendance);

            redirectAttrs.addFlashAttribute("message", "Attendance recorded successfully");
            redirectAttrs.addFlashAttribute("error", false);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error recording attendance: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/attendance";
    }

    @PostMapping("/attendance/update-status")
    public String updateAttendanceStatus(
            @RequestParam int id,
            @RequestParam String newStatus,
            RedirectAttributes redirectAttrs) {
        try {
            // Validate status
            if (!newStatus.equals("Present") && !newStatus.equals("Absent")) {
                redirectAttrs.addFlashAttribute("message", "Invalid status. Must be Present or Absent");
                redirectAttrs.addFlashAttribute("error", true);
                return "redirect:/attendance";
            }

            java.util.Optional<java.util.Map<String, Object>> attendance = attendanceStore.stream()
                .filter(a -> (int) a.get("id") == id)
                .findFirst();

            if (attendance.isPresent()) {
                attendance.get().put("status", newStatus);
                redirectAttrs.addFlashAttribute("message", "Attendance status updated successfully");
                redirectAttrs.addFlashAttribute("error", false);
            } else {
                redirectAttrs.addFlashAttribute("message", "Attendance record not found");
                redirectAttrs.addFlashAttribute("error", true);
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error updating attendance: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/attendance";
    }

    @PostMapping("/attendance/delete")
    public String deleteAttendance(
            @RequestParam int id,
            RedirectAttributes redirectAttrs) {
        try {
            boolean removed = attendanceStore.removeIf(a -> (int) a.get("id") == id);
            
            if (removed) {
                redirectAttrs.addFlashAttribute("message", "Attendance record deleted successfully");
                redirectAttrs.addFlashAttribute("error", false);
            } else {
                redirectAttrs.addFlashAttribute("message", "Attendance record not found");
                redirectAttrs.addFlashAttribute("error", true);
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error deleting attendance: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/attendance";
    }
}
