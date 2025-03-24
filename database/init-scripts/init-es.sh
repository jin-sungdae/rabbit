#!/bin/bash
curl -X PUT "http://elasticsearch:9200/search-index" -H 'Content-Type: application/json' -d '{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "keyword": { "type": "text" },
      "created_at": { "type": "date" }
    }
  }
}'
