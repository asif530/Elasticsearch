package com.elasticsearch.elastic.ElasticQuery.ElasticsearchOperations;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.DoubleTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.FilterAggregate;
import com.elasticsearch.elastic.Result.SearchHits;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("elkOperations")
public class OperationController {
    private ElasticsearchOperations elasticsearchOperations;

    private SearchHits result;

    @Value("${elk-index}")
    private String elkIndex;

    //private String elasticIndex = "hotel";
    private String elasticIndex = "index_demo_phx_property_v2";

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

      /*  nativeQueryBuilder.withQuery(
                q-> q.bool(
                bq->bq.should(
                sq->sq.terms(
                t-> t.field("bed_type").terms(term -> term.value(bed_groups))
        ))));*/
        //Aggregation on fields
        aggregationOnArrayType(nativeQueryBuilder);

        NativeQuery nativeQuery = nativeQueryBuilder.build();

        AggregationsContainer elastic = elasticsearchOperations.search(nativeQuery, SearchHits.class, IndexCoordinates.of(elasticIndex)).getAggregations();
        transformResponse(elastic);
        List<?> searchHits =  elasticsearchOperations.search(nativeQuery, SearchHits.class, IndexCoordinates.of(elasticIndex)).getSearchHits().stream().map(SearchHit::getContent).toList();
        searchHits.stream().filter(x-> Boolean.parseBoolean(x.toString()));
    }

    private void aggregationOnArrayType(NativeQueryBuilder nativeQueryBuilder){
        //NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withAggregation("filterS", new Aggregation.Builder().filter(a->a.term(m->m.field("country_code").value("BD"))).aggregations("star_rating", Aggregation.of(agg-> agg.terms(t->t.field("star_rating")))).build());
        //nativeQueryBuilder.withAggregation("bed_type_count", Aggregation.of(agg-> agg.terms(t->t.field("bed_type"))));
        //nativeQueryBuilder.withAggregation("star_rating", Aggregation.of(agg-> agg.terms(t->t.field("star_rating"))));
        //NativeQuery nativeQuery = nativeQueryBuilder.build();

       /* List<?> searchHits = Collections.singletonList(elasticsearchOperations.search(nativeQuery, SearchHits.class, IndexCoordinates.of("hotel")));
        searchHits.stream().forEach(Object::toString);*/
    }

    public JsonNode transformResponse(AggregationsContainer container) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode transformedResponse = mapper.createObjectNode();
        Map<String, ElasticsearchAggregation> mapAggregation = ((ElasticsearchAggregations) container).aggregationsAsMap();
        List<ElasticsearchAggregation> aggregationList = ((ElasticsearchAggregations) container).aggregations();
        Object result = aggregationList.get(0).aggregation().getAggregate()._get();

        if(result instanceof FilterAggregate){

            Aggregate dterm = ((FilterAggregate) result).aggregations().get("star_rating");
            JsonNode originalJsonNode = mapper.readTree(dterm.dterms().buckets().toString());
        }


        return null;
    }

}
