package com.spring.companyms.clients;

import com.spring.companyms.external.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("jobms")
public interface JobClient {
    @PostMapping("/job/getJobsByIds")
    public List<Job> getJobsByIds(List<Long> jobIds);
}
