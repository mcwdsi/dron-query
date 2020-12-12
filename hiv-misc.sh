java -Xms16G -Xmx16G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit \
\
-query "'drug product' and (has_proper_part some ('has granular part' some DRON_00018974))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00014193))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020283))" \
\
-output Maraviroc.txt,Enfuvirtide.txt,Cobicistat.txt, \
\
1>hiv-misc-$2.out 2>hiv-misc-$2.err
