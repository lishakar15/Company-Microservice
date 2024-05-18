package com.spring.companyms.dto;

import com.spring.companyms.entity.Company;
import com.spring.companyms.external.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyJobDetails {

    private Long companyId;
    private String companyName;
    private List<Job> jobList;
}
