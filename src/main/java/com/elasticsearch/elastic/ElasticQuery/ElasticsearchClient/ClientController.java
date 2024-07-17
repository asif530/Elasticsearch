package com.elasticsearch.elastic.ElasticQuery.ElasticsearchClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.elastic.Result.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("elkClient")
public class ClientController {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private SearchResponse<SearchHits> searchHits;

    @Value("${elk-index}")
    private String elkIndex;

    /**
     * <ul>
     *     <li>Creates an index in elasticsearch and gives a simple mapping</li>
     *     <li><b>todo: Make the mapping dynamic</b></li>
     * </ul>
     */
    @PostMapping("creteIndex")
    public void createIndex(String indexName) throws IOException {
        //todo: make the mapping dynamic
        elasticsearchClient.indices().create(i -> i.index(indexName).mappings(m -> m.properties("name", p -> p.text(t -> t)).properties("id", p -> p.integer(ii -> ii))));
    }

    /**
     * Searches an index based on query. This endpoint performs a <b>search</b> query
     */
    @GetMapping("search")
    private void matchQuery() throws IOException {
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withQuery(q -> q.match(m -> m.field("name").query("Bangladesh")));
        NativeQuery nativeQuery = nativeQueryBuilder.build();


        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(elkIndex)
                .query(nativeQuery.getQuery())
        );

        searchHits = elasticsearchClient.search(searchRequest, SearchHits.class);
        List<SearchHits> results = new ArrayList<>();

        for (Hit<SearchHits> hit : searchHits.hits().hits()) {
            SearchHits searchHit = new SearchHits();
            searchHit.putAll(hit.source());
            results.add(searchHit);
        }

        for (SearchHits hit : results) {
            System.out.println(hit);
        }

    }

    /**
     * Delete an index
     */
    @GetMapping("delete")
    public void deleteIndex(String indexName) throws IOException {
        elasticsearchClient.indices().delete(d -> d.index(indexName));
    }

    /**
     * Delete a document from index using id (primary attribute)
     */
    @GetMapping("deleteById") //test needed
    public void deleteById(String indexName, Integer id) throws IOException {
        elasticsearchClient.delete(d -> d.index(indexName).id(id.toString()));
    }

}
