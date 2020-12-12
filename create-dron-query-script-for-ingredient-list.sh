#!/bin/bash

echo "java -Xms32G -Xmx32G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl \"$1\" -from_file -reasoner hermit \\"
echo "\\"
echo "-query "

while read -r LINE
do
ID=$(echo $LINE | cut -d',' -f2)
FNAME=$(echo $LINE | cut -d',' -f1)
FNAMEL="$FNAMEL,$FNAME"
echo "\"'drug product' and (has_proper_part some ('has granular part' some $ID))\",\\"
done < $2
echo "\\"
echo "-output \"$FNAMEL\" \\"
echo "\\"
echo "1>dron-query-$3.out 2>dron-query-$3.err"

