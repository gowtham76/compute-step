#!/bin/bash

SRC="http://localhost:9200"
DEST="http://localhost:9201"

indices=$(curl -s $SRC/_cat/indices?h=index | grep logs)

for index in $indices; do
  echo "Syncing index: $index"

  curl -s -X POST "$DEST/_reindex" \
    -H "Content-Type: application/json" \
    -d "{
      \"source\": {
        \"remote\": {
          \"host\": \"$SRC\"
        },
        \"index\": \"$index\"
      },
      \"dest\": {
        \"index\": \"$index\"
      },
      \"conflicts\": \"proceed\"
    }"

  echo ""
done
