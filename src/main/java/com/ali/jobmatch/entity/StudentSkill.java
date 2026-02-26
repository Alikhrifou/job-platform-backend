package com.ali.jobmatch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkill {

    @EmbeddedId
    private StudentSkillId id = new StudentSkillId();

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private StudentProfile student;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(nullable = false)
    @Min(1) @Max(5)
    private Integer level;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentSkillId implements java.io.Serializable {
        private Long studentId;
        private Long skillId;
    }
}
