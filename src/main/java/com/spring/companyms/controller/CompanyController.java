package com.spring.companyms.controller;

import com.spring.companyms.clients.JobClient;
import com.spring.companyms.entity.Company;
import com.spring.companyms.external.Job;
import com.spring.companyms.service.CompanyService;
import jakarta.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JobClient jobClient;
    private Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

    @PostMapping("/saveAllCompanies")
    @ResponseBody
    public List<Company> saveAllCompanies(@RequestBody List<Company> companies)
    {
        return companyService.saveAllCompanies(companies);
    }
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody()
    @GetMapping("/healthCheck")
    public String healthCheck()
    {
        return "Health:Good";
    }
    @GetMapping("/getAllCompanies")
    @ResponseBody
    public List<Company> getAllCompanies()
    {
        return companyService.getAllCompanies();
    }
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex)
    {
        System.out.println("Error occured "+ex);
    }
    @GetMapping("getJobsByCompId/{companyId}")
    public ResponseEntity<List<Job>> getJobsByCompanyIdUsingRestTempl(@PathVariable Long companyId)
    {
        List<Job>jobList = null;
        List<List<Long>>  jobIds = companyService.getJobIdsByCompany(companyId);
        //RestTemplate restTemplate = new RestTemplate(); //Using LoadBalanced RestTemplate
        if(!jobIds.isEmpty())
        {
            //calling job microservice post request using RestTemplate
//            jobList = restTemplate.postForObject("http://localhost:8080/getJobsByIds",jobIds.get(0),
//                    List.class);
            jobList = restTemplate.postForObject("jobms/getJobsByIds",jobIds.get(0),
                    List.class); //HTTP call with service name in the URL
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(jobList);
        }
       return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);//Another way of creating ResponseEntity
    }
    @GetMapping("getJobsByCompanyId/{companyId}")
    public ResponseEntity<List<Job>> getJobsByCompanyIdUsingFeignClient(@PathVariable Long companyId)
    {
        List<Job> jobList = null;
        List<List<Long>> jobIds = companyService.getJobIdsByCompany(companyId);
        if(!jobIds.isEmpty())
        {
            jobList = jobClient.getJobsByIds(jobIds.get(0));//Inter Communication using Feign Client
        }
        return new ResponseEntity<> (jobList,HttpStatus.ACCEPTED);
    }

}
