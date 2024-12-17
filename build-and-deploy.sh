mvn clean package
cd deployment
./submit-build.sh
./delete-application.sh
./create-application.sh
