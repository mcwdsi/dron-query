java -Xms8G -Xmx8G -cp ./target/lib/*:./target/classes/:. edu/ufl/bmi/ontology/dronquery/DronDlQuery -purl "$1" -from_file -reasoner hermit \
\
-query "'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive)))",\
"'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive) and (has_proper_part some ('has granular part' some ('is bearer of' some 'beta blocker')))))",\
"'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive) and (has_proper_part some ('has granular part' some ('is bearer of' some 'ca channel blocker')))))",\
"'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive) and (has_proper_part some ('has granular part' some ('is bearer of' some 'ACE inhibitor')))))",\
"'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive) and (has_proper_part some ('has granular part' some ('is bearer of' some ARB)))))",\
"'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive) and (has_proper_part some ('has granular part' some ('is bearer of' some 'nkcc2 inhibitor')))))",\
"'packaged drug product' and (has_proper_part some ('drug product' and ('is bearer of' some anti-hypertensive) and (has_proper_part some ('has granular part' some ('is bearer of' some 'thiazide / thiazide like')))))" \
\
-output all-antihypertensive-ndcs.txt,beta-blocker-ndcs.txt,ca-channel-inhibitor-ndcs.txt,ace-inhibitor-ndcs.txt,arb-ndcs.txt,nkcc2-inhibitor-ndcs.txt,thiazide-ndcs.txt \
\
1>anti-hypertensive-ndc-$2.out 2>anti-hypertensive-ndc-$2.err
