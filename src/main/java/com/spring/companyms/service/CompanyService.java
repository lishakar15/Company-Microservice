package com.spring.companyms.service;

import com.spring.companyms.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    public List<Company> saveAllCompanies(List<Company> jobs);
    public List<Company> getAllCompanies();
    public List<List<Long>> getJobIdsByCompany(Long companyId);
    public Optional<Company> getCompanyById(Long companyId);
}
