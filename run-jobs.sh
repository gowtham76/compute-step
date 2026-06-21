#!/bin/bash

while true
do
    echo "[$(date)] Running RWA..."
    curl -s http://localhost:8091/run/RWA

    echo
    echo "[$(date)] Running ECL..."
    curl -s http://localhost:8091/run/ECL

    echo
    echo "[$(date)] Running LTV..."
    curl -s http://localhost:8091/run/LTV

    echo
    echo "Sleeping for 5 minutes..."
    sleep 300
done
