package com.spring.companyms.controller;

import com.spring.companyms.clients.JobClient;
import com.spring.companyms.dto.CompanyJobDetails;
import com.spring.companyms.entity.Company;
import com.spring.companyms.external.Job;
import com.spring.companyms.service.CompanyService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.management.RuntimeMBeanException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JobClient jobClient;
    private Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);
    private Integer attempt =0;

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
    @GetMapping("/getCompanyById/{companyId}")
    public Company getCompanyById(@PathVariable Long companyId)
    {
        Optional<Company> optional = companyService.getCompanyById(companyId);
        if(optional == null)
        {
            LOGGER.error("getCompanyById company details doesn't exists for Conpany id "+companyId);
            return new Company();
        }
        return optional.isPresent()? optional.get(): new Company();
    }
    /*@ExceptionHandler(Exception.class)
    public void handleException(Exception ex)
    {
        System.out.println("Error occured "+ex);
    }*/
    @GetMapping("/getJobsByCompId/{companyId}")
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
    @GetMapping("/getJobsByCompanyId/{companyId}")
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
    @CircuitBreaker(name="companyBreaker")
    @GetMapping("/getCompanyJobDetails/{companyId}")
    public ResponseEntity<CompanyJobDetails> getCompanyJobDetails(@PathVariable Long companyId) {
        Company company = getCompanyById(companyId);
        CompanyJobDetails companyJobDetails= null;
        ResponseEntity<List<Job>> responseEntity = getJobsByCompanyIdUsingFeignClient(companyId);
        if(responseEntity.getStatusCode()==HttpStatus.ACCEPTED && company != null)
        {
            companyJobDetails = new CompanyJobDetails();
            companyJobDetails.setCompanyId(company.getCompanyId());
            companyJobDetails.setCompanyName(company.getCompanyName());
            if(responseEntity != null)
            {
                companyJobDetails.setJobList(responseEntity.getBody());
            }
        }
        return new ResponseEntity<>(companyJobDetails,HttpStatus.ACCEPTED);

    }
    //CircuitBreaker
    @GetMapping("/getAllJobs")
    @CircuitBreaker(name="companyBreaker",fallbackMethod = "fallbackMethod")
    public List<Job> getAllJobs()
    {
        return jobClient.getAllJobs();
    }
    //Fallback method with same return type of original method
    public List<Job> fallbackMethod(Exception ex){
        System.out.println("Fallback Method Executed. Job Microservice is down...");
        Job job = new Job(0L,"NA", 0L);//0L default Long value for jobId and companyId
        return new ArrayList<Job>(Arrays.asList(job)); //Returning dummy data to avoid other service failures.
    }
    //Retry Technique
    @GetMapping("/getAllJobsRetry")
    @Retry(name="companyBreaker",fallbackMethod = "fallbackMethod")
    public List<Job> getAllJobsForRetry()
    {
        System.out.println("Retry Attempt = "+ ++attempt);
        return jobClient.getAllJobs();
    }

    //Rate Limiter Technique
    @GetMapping("/getAllJobsRetry")
    @RateLimiter(name = "companyBreaker",fallbackMethod = "fallbackMethod")
    public List<Job> getAllJobsForRateLimiter()
    {
        return jobClient.getAllJobs();
    }




}
