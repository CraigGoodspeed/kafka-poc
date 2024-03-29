mvn -f ../dependent-data/ clean
mvn -f ../app/ clean
mvn -f ../Common/ clean
mvn -f ../dependent-data/ avro:schema
mvn -f ../app/ avro:schema
mvn -f ../Common/ install
mvn -f ../dependent-data/ install
mvn -f ../app/ install
docker-compose -f "../docker/docker-compose.yml" up -d
python3 ensure_registry_up.py
mvn -f ../dependent-data/ schema-registry:register
mvn -f ../app/ schema-registry:register
python3 application_startup.py