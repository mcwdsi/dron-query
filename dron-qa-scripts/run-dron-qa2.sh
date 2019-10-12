./dron-qa-scripts/run-all-dron-qa-dl-queries.sh "$1" $2

cat dp-vs-pdp-results-$2.txt | wc -l
cat chem-entity-results-$2.txt | wc -l
cat processed-material-results-$2.txt | wc -l
cat drug-product-results-$2.txt | wc -l
cat packaged-drug-product-results-$2.txt | wc -l
