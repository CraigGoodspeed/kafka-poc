mvn -f ../dependent-data/ avro:schema
mvn -f ../app/ avro:schema
docker-compose -f "../docker/docker-compose.yml" up -d
python3 ensure_registry_up.py
mvn -f ../dependent-data/ schema-registry:register
mvn -f ../app/ schema-registry:register
python3 application_startup.py