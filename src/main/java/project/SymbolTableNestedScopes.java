package main.java.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTableNestedScopes{

    Map<Integer, Map<Integer , Map<String, Var>>> scopemap = new HashMap<>();
    Map<Integer, Integer> currentscopes = new HashMap<>();
    Integer currentscope = 0;
    private List<Integer> sizes = new ArrayList<>();
    private int size;

    public SymbolTableNestedScopes(){
        scopemap.put(currentscope, new HashMap<>());
    }

    public void openScope(int thread) {
    	System.out.println("open");
        currentscopes.put(thread, currentscopes.get(thread)+1);
        scopemap.put(thread, new HashMap<>());
        scopemap.get(thread).put(currentscopes.get(thread), new HashMap<>());
        sizes.add(size);
        size = 0;
    }

    public void closeScope(){
    	System.out.println("close");
        if(currentscope == 0){
            throw new RuntimeException();
        } else {
            scopemap.remove(currentscope);
            size = sizes.get(currentscope - 1);
            sizes.remove(currentscope);
            currentscope--;
        }
    }

    public boolean add(String id, Type type, int thread) {
    	System.out.println("add " + id);
        if(contains(id, thread)){
            return false;
        }
        scopemap.get(thread).get(currentscope).put(id, new Var(type, size));
        size += type.size();
        return true;
    }
    
	public Var getVar(String id, int thread) {
    	System.out.println("get " + id);
		for( Map<String, Var> s:scopemap.get(thread).values()){
            if(s.containsKey(id)){
                return s.get(id);
            }
        }
		return null;
	}

    public boolean contains(String id, int thread) {
    	System.out.println("contains " + id);
        for( Map<String, Var> s:scopemap.get(thread).values()){
            if(s.containsKey(id)){
                return true;
            }
        }
        return  false;
    }
    
}