set -u 
set -e

REPO=us-central1-docker.pkg.dev/solutionsarchitect-01/flink-replication-anonymization
VERSION=0.0.11

cp ../target/flink-replication-anonymization-1.0-SNAPSHOT.jar docker/
gcloud builds submit \
  --tag $REPO/flink-replication-anonymization:$VERSION docker
