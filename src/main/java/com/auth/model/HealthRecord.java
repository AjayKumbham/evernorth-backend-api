package com.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "health_records")
@IdClass(HealthRecordId.class)
public class HealthRecord {
    
    @Id
    @Column(name = "member_id")
    private String memberId;
    
    @Id
    @Column(name = "record_no")
    private Integer recordNo;
    
    @Column(name = "health_condition", length = 100, nullable = false)
    private String healthCondition;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;
}