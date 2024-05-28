package com.spring.companyms.service;


import com.spring.companyms.entity.Company;
import com.spring.companyms.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    @Override
    public List<Company> saveAllCompanies(List<Company> companies) {

        return companyRepository.saveAll(companies);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public List<List<Long>> getJobIdsByCompany(Long companyId) {
        return companyRepository.getJobIdsByCompany(companyId);
    }

    @Override
    public Optional<Company> getCompanyById(Long companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public List<Company> getCompaniesByCompanyId(List<Long> companyIds) {
        return companyRepository.getCompanyByCompanyIdIn(companyIds);
    }

    @Override
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }
}
