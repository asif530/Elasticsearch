package com.elasticsearch.elastic.ElasticQuery;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import com.elasticsearch.elastic.Result.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
@RestController
@RequestMapping("elk")
public class Controller {
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private SearchResponse<SearchHits> searchHits;

    private String elkIndex = "partial_search";
    @GetMapping("search")
    private void matchQuery() throws IOException {
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withQuery(q->q.match(m->m.field("name").query("Bangladesh")));
        NativeQuery nativeQuery = nativeQueryBuilder.build();


        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(elkIndex)
                .query( nativeQuery.getQuery())
        );

        searchHits = elasticsearchClient.search(searchRequest,SearchHits.class);
        List<SearchHits> results = new ArrayList<>();

        for (Hit<SearchHits> hit : searchHits.hits().hits()) {
            SearchHits searchHit = new SearchHits();
            searchHit.putAll(hit.source());
            results.add(searchHit);
        }

        for (SearchHits hit : results) {
            System.out.println(hit);
        }

      //

    }


    public void createIndex() throws IOException {
        elasticsearchClient.indices().create(i-> i.index("products").mappings(m->m.properties("name",p->p.text(t->t)).properties("id",p->p.integer(ii->ii))));
    }

    public void deleteIndex() throws IOException {
        elasticsearchClient.indices().delete(d->d.index("products"));
    }

    public void deleteById() throws IOException {
        elasticsearchClient.delete(d->d.index("products").id("1"));
    }

}
