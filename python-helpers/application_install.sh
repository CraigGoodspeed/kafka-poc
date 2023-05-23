mvn -f ../dependent-data/ avro:schema
mvn -f ../app/ avro:schema
docker-compose -f "../docker/docker-compose.yml" up -d
sleep 5
mvn -f ../dependent-data/ schema-registry:register
mvn -f ../app/ schema-registry:register
python3 application_startup.py