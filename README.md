````
ElasticSearch with Java
Index & Mapping
================
PUT /hotel
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "property_id": { "type": "long" },
      "star_rating": { "type": "double" },
      "bed_type": { "type": "keyword" },
      "brand_name": { "type": "keyword" },
      "facilities": { "type": "keyword" }
    }
  }
}

Data
=====
POST /hotel/_doc
{"property_id": 1, "star_rating": 4.5, "bed_type": ["King Bed", "Queen Bed"], "brand_name": "Hilton", "facilities": ["Free Wi-Fi", "Gym", "Pool"]}
 
POST /hotel/_doc
{"property_id": 2, "star_rating": 3.8, "bed_type": ["Double Bed"], "brand_name": "Marriott", "facilities": ["Parking", "Free Breakfast"] }
 
 POST /hotel/_doc
{"property_id": 3, "star_rating": 4.0, "bed_type": ["Queen Bed", "King & Queen Bed"], "brand_name": "Hyatt", "facilities": ["Gym", "Spa", "Free Wi-Fi"] }
 
 POST /hotel/_doc
{"property_id": 4, "star_rating": 4.9, "bed_type": ["King Bed"], "brand_name": "Sheraton", "facilities": ["Pool", "Gym", "Valet Parking"] }

 POST /hotel/_doc
 {"property_id": 5, "star_rating": 3.6, "bed_type": ["Single Bed", "Double Bed"], "brand_name": "Holiday Inn", "facilities": ["Free Wi-Fi", "Parking"] }
 
 POST /hotel/_doc
{"property_id": 6, "star_rating": 4.3, "bed_type": ["King Bed", "Queen Bed"], "brand_name": "Ritz-Carlton", "facilities": ["Spa", "Gym", "Free Breakfast"] }

single data push

Query
=======
GET /hotel/_search
{
  "query": {
    "terms": {
      "bed_type": ["King Bed", "Queen Bed"]
    }
  }
}
is equivalent to (Need to test)

GET /hotel/_search
"query":{ {"bool":{"should":[{"terms":{"bed_type":["King Bed","Queen Bed"]}}]}}}

Aggregation on fields which are array type
===========================================
To perform an aggregation on the bed_type field, which is an array of strings, you can use a terms aggregation. 
Elasticsearch can handle arrays in a way that each element is treated as a separate value for aggregation purposes.
GET /hotel/_search
{
 "aggs": {
   "bed_type_count": {
     "terms": {
       "field": "bed_type",
       "size": 5
     }
   }
 }
}
It will return the aggregation result of bed_type field. 

The change is merged by pulling that branch code in master.