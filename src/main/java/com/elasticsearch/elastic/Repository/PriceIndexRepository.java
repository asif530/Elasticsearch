package com.elasticsearch.elastic.Repository;

import co.elastic.clients.elasticsearch.indices.SlowlogTresholdLevels;
import com.elasticsearch.elastic.Entity.PriceIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;
@Repository
@EnableElasticsearchRepositories
public interface PriceIndexRepository extends ElasticsearchRepository<PriceIndex,Integer> {
    @Query("{ \"bool\": { \"must\": [ { \"match\": { \"country\": \"BD\" } }, { \"match\": { \"date\": \"2025-01-01\" } } ] } }")
    Stream<PriceIndex> getData();

    Stream<PriceIndex> getPriceIndexByAdventurePropertyIdAndCountryAndDate(Integer propertyId,String country,String date);
}
