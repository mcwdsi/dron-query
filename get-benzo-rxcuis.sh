java -Xms16G -Xmx16G -cp "./target/classes:./lib/*" edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit --query="'drug product' and (has_proper_part some ('has granular part' some ('is bearer of' some 'GABA-A agonist')))" --output=benzo-rxcuis.txt 1>benzos-$2.out 2>benzos-$2.err 

