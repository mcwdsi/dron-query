java -Xms16G -Xmx16G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit \
\
-query "'drug product' and (has_proper_part some ('has granular part' some DRON_00816344))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00750787))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020284))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020175))" \
\
-output Bictegravir.txt,Dolutegravir.txt,Elvitegravir.txt,Raltegravir.txt \
\
1>hiv-insti-$2.out 2>hiv-insti-$2.err
