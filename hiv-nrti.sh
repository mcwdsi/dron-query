java -Xms16G -Xmx16G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit \
\
-query "'drug product' and (has_proper_part some ('has granular part' some DRON_00016121))",\
"'drug product' and (has_proper_part some ('has granular part' some CHEBI_490877))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020281))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00017772))",\
"'drug product' and (has_proper_part some ('has granular part' some CHEBI_63581))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00730858))",\
"'drug product' and (has_proper_part some ('has granular part' some DRON_00020282))",\
"'drug product' and (has_proper_part some ('has granular part' some CHEBI_10110))" \
\
-output Abacavir.txt,Didanosine.txt,Emtricitabine.txt,Lamivudine.txt,Stavudine.txt,Tenofovir_alafenamide.txt,Tenofovir_disoproxil_fumarate.txt,Zidovudine.txt \
\
1>hiv-nrti-$2.out 2>hiv-nrti-$2.err
