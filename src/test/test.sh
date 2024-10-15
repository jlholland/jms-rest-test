#!/bin/bash
for i in $(seq 1 20)
do
	curl http://localhost:8080/hello &
done
