java -Xms12G -Xmx12G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner elk -i \
\
-query "GEO_000000372 and (BFO_0000137 value GEO_000000345)" \
\
-output everything-part-of-earth-transitively.txt \
\
1>earth-transitive-parts-$2.out 2>earth-transitive-parts-$2.err
