package com.elasticsearch.elastic.ElasticQuery.ElasticsearchOperations;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.elastic.Result.SearchHits;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("elkOperations")
public class OperationController {
    private ElasticsearchOperations elasticsearchOperations;

    private SearchHits result;

    @Value("${elk-index}")
    private String elkIndex;

    public OperationController(ElasticsearchOperations elasticsearchOperations,SearchHits result) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.result = result;
    }

    @GetMapping("search")
    private void matchQuery() throws IOException {
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withQuery(q -> q.match(m -> m.field("name").query("Bangladesh")));
        NativeQuery nativeQuery = nativeQueryBuilder.build();


       List<?> searchHits =  elasticsearchOperations.search(nativeQuery, SearchHits.class, IndexCoordinates.of(elkIndex)).getSearchHits().stream().map(SearchHit::getContent).toList();


    }

}
