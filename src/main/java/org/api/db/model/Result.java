package org.api.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable{
	
    public String status = "";
    public String desc="";
    public int count = -1;
    //public int count_total = -1;
    //public int pages = -1;
    public List<Object> results = new ArrayList<>();

}