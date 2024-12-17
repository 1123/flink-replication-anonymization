OLD_MINOR=$(cat VERSION | sed 's/0.0.//')
NEW_MINOR=$(echo $((OLD_MINOR+ 1)))
OLD_VERSION=0.0.$OLD_MINOR
NEW_VERSION=0.0.$NEW_MINOR
sed -i '.bak' 's/'$OLD_VERSION'/'$NEW_VERSION'/g' *.json *.sh
rm *.bak
echo $NEW_VERSION > VERSION
