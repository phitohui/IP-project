package com.example.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MeetingsController {
    // In-memory storage for meetings (using a mutable list)
    private static final java.util.List<java.util.Map<String, Object>> meetingStore = 
        new java.util.ArrayList<>(java.util.List.of(
            java.util.Map.of("id", 1, "date", "2025-11-10", "title", "Annual General Meeting", "minutes", true, "minutesFile", "AGM_Minutes.pdf"),
            java.util.Map.of("id", 2, "date", "2025-12-01", "title", "Committee Meeting", "minutes", false, "minutesFile", ""),
            java.util.Map.of("id", 3, "date", "2025-12-15", "title", "Budget Review", "minutes", false, "minutesFile", "")
        ));

    @GetMapping("/meetings")
    public String meetings(Model model){
        model.addAttribute("title", "Meeting Management");
        model.addAttribute("meetings", meetingStore);
        return "meetings";
    }

    @GetMapping("/meetings/new")
    public String showNewMeetingForm(Model model) {
        model.addAttribute("title", "Create New Meeting");
        model.addAttribute("isNew", true);
        return "meetings/form";
    }

    @PostMapping("/meetings/save")
    public String saveMeeting(
            @RequestParam String date,
            @RequestParam String title,
            RedirectAttributes redirectAttrs) {
        try {
            // Generate new ID
            int newId = meetingStore.stream()
                .mapToInt(m -> (int) m.get("id"))
                .max()
                .orElse(0) + 1;

            // Create new meeting
            java.util.Map<String, Object> newMeeting = new java.util.HashMap<>();
            newMeeting.put("id", newId);
            newMeeting.put("date", date);
            newMeeting.put("title", title);
            newMeeting.put("minutes", false);
            newMeeting.put("minutesFile", "");

            meetingStore.add(newMeeting);

            redirectAttrs.addFlashAttribute("message", "Meeting created successfully");
            redirectAttrs.addFlashAttribute("error", false);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error creating meeting: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/meetings";
    }

    @GetMapping("/meetings/upload-minutes")
    public String showUploadMinutesForm(@RequestParam int id, Model model) {
        java.util.Optional<java.util.Map<String, Object>> meeting = meetingStore.stream()
            .filter(m -> (int) m.get("id") == id)
            .findFirst();
        
        if (meeting.isEmpty()) {
            return "redirect:/meetings";
        }
        
        model.addAttribute("title", "Upload Meeting Minutes");
        model.addAttribute("meeting", meeting.get());
        return "meetings/upload-minutes";
    }

    @PostMapping("/meetings/save-minutes")
    public String saveMinutes(
            @RequestParam int id,
            @RequestParam String minutesFileName,
            RedirectAttributes redirectAttrs) {
        try {
            java.util.Optional<java.util.Map<String, Object>> meeting = meetingStore.stream()
                .filter(m -> (int) m.get("id") == id)
                .findFirst();
            
            if (meeting.isPresent()) {
                java.util.Map<String, Object> m = meeting.get();
                m.put("minutes", true);
                m.put("minutesFile", minutesFileName);
                
                redirectAttrs.addFlashAttribute("message", "Meeting minutes uploaded successfully");
                redirectAttrs.addFlashAttribute("error", false);
            } else {
                redirectAttrs.addFlashAttribute("message", "Meeting not found");
                redirectAttrs.addFlashAttribute("error", true);
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("message", "Error uploading minutes: " + e.getMessage());
            redirectAttrs.addFlashAttribute("error", true);
        }
        return "redirect:/meetings";
    }
}
