package com.spring.companyms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "company_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
        @Id
        private Long companyId;
        private String companyName;
        private List<Long> jobIds;
}
