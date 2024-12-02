package com.elasticsearch.elastic.ElasticQuery.ElasticsearchJPAController;

import com.elasticsearch.elastic.Service.JPAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("jpaClient")
public class JPAController {
    @Autowired
    private JPAService jpaService;

    @GetMapping("/fetchData")
    public void fetchData(){
        jpaService.fetchData();
    }
}
