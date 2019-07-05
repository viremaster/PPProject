package main.java.project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//Symbol table to store the address of variables.

public class SymbolTableNestedScopesInteger{

	//map of scopes sorted by highest to lowest for ease of use.
    Map<Integer , Map<String, String>> scopemap = new TreeMap<>(Collections.reverseOrder());
    //Current scope that is being used.
    private Integer currentscope = 0;

    //Create first scope.
    public SymbolTableNestedScopesInteger(){
        scopemap.put(currentscope, new HashMap<>());
    }

    //Opens a new scope.
    public void openScope() {
        currentscope++;
        scopemap.put(currentscope, new HashMap<>());
    }

    //Closes the scope.
    public void closeScope(){
        if(currentscope == 0){
            throw new RuntimeException();
        } else {
            scopemap.remove(currentscope);
            currentscope--;
        }
    }

    //Add a new variable to the current scope.
    public boolean add(String id, String val) {
        if(contains(id)){
            return false;
        }
        scopemap.get(currentscope).put(id, val);
        return true;
    }
    
    //gets the address of a variable going through the top scope first and then down.
	public String getInt(String id) {
		for( Map<String, String> s:scopemap.values()){
            if(s.containsKey(id)){
                return s.get(id);
            }
        }
		return null;
	}

	//Check if a variable is in the scope.
    public boolean contains(String id) {
        for( Map<String, String> s:scopemap.values()){
            if(s.containsKey(id)){
                return true;
            }
        }
        return  false;
    }
    
}