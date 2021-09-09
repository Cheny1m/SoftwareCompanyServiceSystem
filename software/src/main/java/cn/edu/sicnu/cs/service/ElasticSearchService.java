package cn.edu.sicnu.cs.service;

import java.io.IOException;

public interface ElasticSearchService {

    public String searchsuggest(String keyword,int pagenum,int pagesize) throws IOException;
}
