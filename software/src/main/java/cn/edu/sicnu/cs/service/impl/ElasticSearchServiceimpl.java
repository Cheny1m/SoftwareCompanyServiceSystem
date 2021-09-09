package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.model.Userform;
import cn.edu.sicnu.cs.service.CustomersService;
import cn.edu.sicnu.cs.service.ElasticSearchService;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ElasticSearchServiceimpl implements ElasticSearchService {


    @Resource
    RestHighLevelClient client;


    @Resource
    CustomersService customersService;

    @Override
    public String searchsuggest(String keyword, int pagenum, int pagesize) throws IOException {
//        List<Userform> list = customersService.finalluserform();
//
//        CreateIndexRequest createIndexRequest = new CreateIndexRequest("userform1");
//
//        client.indices().create(createIndexRequest,RequestOptions.DEFAULT);
//
//        BulkRequest bulkRequest = new BulkRequest();
//
//        for (int i = 0;i < list.size();i++){
//            bulkRequest.add(
//                    new IndexRequest("userform1").source(JSON.toJSONString(list.get(i)), XContentType.JSON)
//            );
//        }
//
//        client.bulk(bulkRequest,RequestOptions.DEFAULT);

        pagenum = (pagenum-1)*pagesize;

        SearchRequest request = new SearchRequest("userform");

        SearchSourceBuilder builder = new SearchSourceBuilder();

        if(keyword.equals("")){
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            builder.query(matchAllQueryBuilder);
            builder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            builder.from(pagenum);
            builder.size(pagesize);
            request.source(builder);
            SearchResponse response = client.search(request,RequestOptions.DEFAULT);
            return JSON.toJSONString(response.getHits());
        }else{
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("fcontent",keyword);
            builder.query(matchQueryBuilder);
            builder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            builder.from(pagenum);
            builder.size(pagesize);
            request.source(builder);
            SearchResponse response = client.search(request,RequestOptions.DEFAULT);
            return JSON.toJSONString(response.getHits());
        }

    }
}
