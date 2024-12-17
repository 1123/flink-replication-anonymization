mvn clean package
cd deployment
./delete-application.sh
./update-version.sh
./submit-build.sh
./create-application.sh
git add .
git commit -m "new version: $(CAT VERSION)"
