package com.ali.jobmatch.service;

import com.ali.jobmatch.entity.Skill;
import com.ali.jobmatch.exception.BadRequestException;
import com.ali.jobmatch.exception.ResourceNotFoundException;
import com.ali.jobmatch.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public Skill createSkill(String name, String category, String description) {
        if (skillRepository.existsByName(name)) {
            throw new BadRequestException("Skill already exists");
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);
        skill.setDescription(description);

        return skillRepository.save(skill);
    }

    public Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
    }

    public Skill getSkillByName(String name) {
        return skillRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill updateSkill(Long skillId, String name, String category, String description) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        skill.setName(name);
        skill.setCategory(category);
        skill.setDescription(description);

        return skillRepository.save(skill);
    }

    public void deleteSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        skillRepository.delete(skill);
    }
}
