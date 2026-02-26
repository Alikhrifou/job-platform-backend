package com.ali.jobmatch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSkill {

    @EmbeddedId
    private JobSkillId id = new JobSkillId();

    @ManyToOne
    @MapsId("jobId")
    @JoinColumn(name = "job_id")
    private JobOffer job;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(nullable = false)
    @Min(1) @Max(5)
    private Integer requiredLevel;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobSkillId implements java.io.Serializable {
        private Long jobId;
        private Long skillId;
    }
}
