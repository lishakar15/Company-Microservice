package com.spring.companyms.rabbitmq;

import com.netflix.discovery.converters.Auto;
import com.spring.companyms.dto.JobMessage;
import com.spring.companyms.service.CompanyService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobMessageConsumer {
    @Autowired
    public CompanyService companyService;

    @RabbitListener(queues = "companyJobQueue")
    public void consumeJobMessage(JobMessage jobMessage)
    {
        System.out.println("jobMessage ===> "+jobMessage);
        //save job to corresponding company
    }

}
