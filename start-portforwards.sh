#!/bin/bash

nohup kubectl port-forward -n logging svc/kibana 5601:5601 > kibana-portforward.log 2>&1 &
nohup kubectl port-forward -n logging svc/elasticsearch 9200:9200 > elasticsearch-portforward.log 2>&1 &
nohup kubectl port-forward service/compute-step-service 8091:80 > compute-step-portforward.log 2>&1 &
