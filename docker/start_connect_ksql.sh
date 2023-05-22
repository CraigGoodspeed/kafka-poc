docker-compose down
wait
docker-compose up -d
wait
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088