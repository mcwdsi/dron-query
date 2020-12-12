java -Xms16G -Xmx16G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit \
\
-query "'drug product' and (has_proper_part some ('has granular part' some DRON_00018681))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020177))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00018831))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00013621))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00016421))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00013466))",\
"'drug product' and (has_proper_part some ('has granular part' some CHEBI_45409))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00016171))",\
"'drug product' and (has_proper_part some ('has granular part' some CHEBI_63628))" \
\
-output Atazanavir.txt,Darunavir.txt,Fosamprenavir.txt,Indinavir.txt,Lopinavir.txt,Nelfinavir.txt,Ritonavir.txt,Saquinavir.txt,Tipranavir.txt \
\
1>hiv-protease-$2.out 2>hiv-protease-$2.err
