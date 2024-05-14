package com.spring.companyms.repository;

import com.spring.companyms.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{

    @Query("select jobIds from Company c where c.companyId =?1")
    public List<List<Long>> getJobIdsByCompany(Long companyId);
}
