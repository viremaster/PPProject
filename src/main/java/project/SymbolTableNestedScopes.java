package main.java.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTableNestedScopes{

    Map<Integer , ArrayList<String>> scopemap = new HashMap<>();
    Integer currentscope = 0 ;

    public SymbolTableNestedScopes(){
        scopemap.put(currentscope, new ArrayList<>());
    }

    public void openScope() {
        currentscope++;
        scopemap.put(currentscope, new ArrayList<>());
    }

    public void closeScope(){
        if(currentscope == 0){
            throw new RuntimeException();
        } else {
            scopemap.remove(currentscope);
            currentscope--;
        }
    }

    public boolean add(String id) {
        if(contains(id)){
            return false;
        }
        scopemap.get(currentscope).add(id);
        return true;
    }

    public boolean contains(String id) {
        for(ArrayList<String> s:scopemap.values()){
            if(s.contains(id)){
                return true;
            }
        }
        return  false;
    }
}