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
@Table(name = "allergy_records")
@IdClass(AllergyRecordId.class)
public class AllergyRecord {
    
    @Id
    @Column(name = "member_id")
    private String memberId;
    
    @Id
    @Column(name = "record_no")
    private Integer recordNo;
    
    @Column(length = 100, nullable = false)
    private String allergies;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;
}