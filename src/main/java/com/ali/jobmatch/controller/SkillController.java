package com.ali.jobmatch.controller;

import com.ali.jobmatch.entity.Skill;
import com.ali.jobmatch.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Skill management endpoints")
@SecurityRequirement(name = "Bearer Token")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @GetMapping
    @Operation(summary = "Get all skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill by ID")
    public ResponseEntity<Skill> getSkill(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkill(id));
    }
}
