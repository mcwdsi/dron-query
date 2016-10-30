package edu.ufl.bmi.ontology.dronquery;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;


public class DronDlQueryTranslator {

    HashMap<String, String> translation;
    String filePathAndName;
    boolean attemptedToLoad;

    public DronDlQueryTranslator(String filePathAndName) {
	translation = new HashMap<String,String>();
	this.filePathAndName = filePathAndName;
	attemptedToLoad = false;
    }

    protected void loadTranslationsFile() throws IOException {
	FileReader fr = new FileReader(filePathAndName);
	LineNumberReader lnr = new LineNumberReader(fr);
	String line;
	while ((line=lnr.readLine())!=null) {
	    String[] flds = line.split("\t");
	    System.out.println(flds[0]);
	    translation.put(flds[0], flds[1]);
	}
    }

    public String translateTerm(String from) {
	if (!attemptedToLoad) {
	    try {
		loadTranslationsFile();
	    }
	    catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	    finally {
		attemptedToLoad = true;
	    }
	}

	return translation.get(from);
    }

    public String translateQuery(String queryTxt) {
	if (!attemptedToLoad) {
	    try {
		loadTranslationsFile();
	    }
	    catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	    finally {
		attemptedToLoad = true;
	    }
	}

	Set<String> keySet = translation.keySet();

	String queryRepl = queryTxt;
	for (String key : keySet) {
	    queryRepl = queryRepl.replaceAll(key, translation.get(key));
	}

	return queryRepl;
    }

}