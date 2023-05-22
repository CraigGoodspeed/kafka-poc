kafka-log-dirs --describe \
--bootstrap-server localhost:29092 \
--describe |  grep '^{' | jq '[.brokers[] | {broker:.broker, size:[.logDirs[].partitions[].size] | add}]'
