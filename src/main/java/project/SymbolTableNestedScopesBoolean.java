package main.java.project;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableNestedScopesBoolean{

    Map<Integer , Map<String, Boolean>> scopemap = new HashMap<>();
    Integer currentscope = 0 ;

    public SymbolTableNestedScopesBoolean(){
        scopemap.put(currentscope, new HashMap<>());
    }

    public void openScope() {
        currentscope++;
        scopemap.put(currentscope, new HashMap<>());
    }

    public void closeScope(){
        if(currentscope == 0){
            throw new RuntimeException();
        } else {
            scopemap.remove(currentscope);
            currentscope--;
        }
    }

    public boolean add(String id, Boolean val) {
        if(contains(id)){
            return false;
        }
        scopemap.get(currentscope).put(id, val);
        return true;
    }

    public boolean contains(String id) {
        for(Map<String, Boolean> s:scopemap.values()){
            if(s.containsKey(id)){
                return true;
            }
        }
        return  false;
    }

    public boolean change(String id, Boolean val) {
        if(!contains(id)){
            return false;
        }
        for(int i = currentscope; i > 0; i++){
            if(scopemap.get(i).containsKey(id)){
                scopemap.get(i).put(id, val);
                return true;
            }
        }
        return false;
    }
}