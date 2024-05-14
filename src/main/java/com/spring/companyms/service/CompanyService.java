package com.spring.companyms.service;

import com.spring.companyms.entity.Company;

import java.util.List;

public interface CompanyService {
    public List<Company> saveAllCompanies(List<Company> jobs);

    public List<Company> getAllCompanies();
    public List<List<Long>> getJobIdsByCompany(Long companyId);
}
