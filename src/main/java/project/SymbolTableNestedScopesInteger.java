package main.java.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SymbolTableNestedScopesInteger{

    Map<Integer , Map<String, String>> scopemap = new TreeMap<>(Collections.reverseOrder());
    private Integer currentscope = 0;
    private List<Integer> sizes = new ArrayList<>();
    private int size;

    public SymbolTableNestedScopesInteger(){
        scopemap.put(currentscope, new HashMap<>());
    }

    public void openScope() {
        currentscope++;
        scopemap.put(currentscope, new HashMap<>());
        sizes.add(size);
        size = 0;
    }

    public void closeScope(){
        if(currentscope == 0){
            throw new RuntimeException();
        } else {
            scopemap.remove(currentscope);
            size = sizes.get(currentscope - 1);
            sizes.remove(currentscope);
            currentscope--;
        }
    }

    public boolean add(String id, String val) {
        if(contains(id)){
            return false;
        }
        scopemap.get(currentscope).put(id, val);
        return true;
    }
    
	public String getInt(String id) {
		for( Map<String, String> s:scopemap.values()){
            if(s.containsKey(id)){
                return s.get(id);
            }
        }
		return null;
	}

    public boolean contains(String id) {
        for( Map<String, String> s:scopemap.values()){
            if(s.containsKey(id)){
                return true;
            }
        }
        return  false;
    }
    
}