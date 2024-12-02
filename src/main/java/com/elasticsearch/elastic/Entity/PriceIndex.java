package com.elasticsearch.elastic.Entity;

import co.elastic.clients.util.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DatabindException;
import lombok.Data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "price-detail-v3")
@JsonIgnoreProperties
@Getter
@Setter
public class PriceIndex {
    @Id
    Integer adventurePropertyId;
    DateTime date;
    Double price;
    @Field("strikeThroughPrice")
    Double strikeThroughPrice;
    DateTime fetchDate;
    String country;
}
