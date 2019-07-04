package main.java.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SymbolTableNestedScopes{

    Map<Integer , Map<String, Type>> scopemap = new TreeMap<>(Collections.reverseOrder());
    private Integer currentscope = 0;
    private List<Integer> sizes = new ArrayList<>();
    private int size;

    public SymbolTableNestedScopes(){
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

    public boolean add(String id, Type type) {
        if(contains(id)){
            return false;
        }
        scopemap.get(currentscope).put(id, type);
        return true;
    }
    
	public Type getType(String id) {
		for( Map<String, Type> s:scopemap.values()){
            if(s.containsKey(id)){
                return s.get(id);
            }
        }
		return null;
	}

    public boolean contains(String id) {
        for( Map<String, Type> s:scopemap.values()){
            if(s.containsKey(id)){
                return true;
            }
        }
        return  false;
    }
    
    public int getScope() {
    	return currentscope;
    }
    
}