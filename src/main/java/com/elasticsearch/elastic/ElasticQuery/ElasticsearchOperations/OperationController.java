package com.elasticsearch.elastic.ElasticQuery.ElasticsearchOperations;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.elasticsearch.elastic.Result.SearchHits;
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
import java.util.Collections;
import java.util.List;

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

    @GetMapping("searchTerm")
    private void termQuery() throws IOException{
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        List <String> bedGroups = new ArrayList<>(List.of(new String[]{"King Bed", "Queen Bed"}));

        List<FieldValue> bed_groups = new ArrayList<>();
        bedGroups.forEach(bd -> bed_groups.add(FieldValue.of(bd)));

        nativeQueryBuilder.withQuery(
                q-> q.bool(
                bq->bq.should(
                sq->sq.terms(
                t-> t.field("bed_type").terms(term -> term.value(bed_groups))
        ))));
        NativeQuery nativeQuery = nativeQueryBuilder.build();
        aggregationOnArrayType();

        List<?> searchHits =  elasticsearchOperations.search(nativeQuery, SearchHits.class, IndexCoordinates.of("hotel")).getSearchHits().stream().map(SearchHit::getContent).toList();
        searchHits.stream().filter(x-> Boolean.parseBoolean(x.toString()));
    }

    private void aggregationOnArrayType(){
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withAggregation("bed_type_count", Aggregation.of(agg-> agg.terms(t->t.field("bed_type"))));
        NativeQuery nativeQuery = nativeQueryBuilder.build();

        List<?> searchHits = Collections.singletonList(elasticsearchOperations.search(nativeQuery, SearchHits.class, IndexCoordinates.of("hotel")));
        searchHits.stream().forEach(Object::toString);
    }

}
