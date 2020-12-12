java -Xms16G -Xmx16G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit \
\
-query "'drug product' and (has_proper_part some ('has granular part' some DRON_00010568))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00017693))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00018993))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00018005))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020128))" \
\
-output Delavirdine.txt,Efavirenz.txt,Etravirine.txt,Nevirapine.txt,Rilpivirine.txt \
\
1>hiv-nnti-$2.out 2>hiv-nnti-$2.err
