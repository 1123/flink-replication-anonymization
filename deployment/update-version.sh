sed -i '.bak' 's/'$1'/'$2'/g' *.json *.sh
rm *.bak
