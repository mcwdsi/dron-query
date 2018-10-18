./dron-qa-scripts/run-chem-entity.sh "$1" $2
./dron-qa-scripts/run-processed-material.sh "$1" $2
./dron-qa-scripts/run-drug-product.sh "$1" $2
./dron-qa-scripts/run-packaged-drug-product.sh "$1" $2

cat chem-entity-results-$2.txt | wc -l
cat processed-material-results-$2.txt | wc -l
cat drug-product-results-$2.txt | wc -l
cat packaged-drug-product-results-$2.txt | wc -l
