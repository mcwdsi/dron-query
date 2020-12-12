java -Xms14G -Xmx14G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit -i \
\
-query "PCO_0000001 and (located_in value APOLLO_SV_00000097)" \
\
-output pops-in-earth-ecosystem-transitively.txt \
\
1>pop-located-transitive-parts-$2.out 2>pop-located-transitive-parts-$2.err
