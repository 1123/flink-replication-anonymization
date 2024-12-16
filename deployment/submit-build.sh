cp ../target/flink-replication-anonymization-1.0-SNAPSHOT.jar docker/
gcloud builds submit \
  --tag us-central1-docker.pkg.dev/solutionsarchitect-01/flink-replication-anonymization/flink-replication-anonymization:0.0.3 docker
