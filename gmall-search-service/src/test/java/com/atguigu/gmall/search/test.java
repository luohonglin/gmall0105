package com.atguigu.gmall.search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

public class test {
    private TransportClient client = null;


    @Test
    public  void getResult() throws Exception{

        SearchResponse response = client.prepareSearch("gmall0105","gmall0105")//创建查询索引,参数productindex表示要查询的索引库为blog、index
//                .setTypes("article")  //设置type
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)//设置查询类型 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询  2.SearchType.SCAN =扫描查询,无序
                .setQuery(QueryBuilders.termQuery("skuName", "华为"))  //设置查询项以及对应的值
                //	        .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // 设置Filter过滤
                .setFrom(0).setSize(20)//设置分页
                .setExplain(true) //设置是否按查询匹配度排序
                //        .addSort("id", SortOrder.DESC)//设置按照id排序
                .execute()
                .actionGet();
        SearchHits hits = response.getHits();
        System.out.println("总数："+hits.getTotalHits());
        for(SearchHit hit: hits.getHits()) {
//            hit.highlightFields();
            if(hit.getSourceAsMap().containsKey("skuName")) {
                System.out.println("source.skuName: " + hit.getSourceAsMap());
            }
        }
        System.out.println(response.toString());
        closeClient();
    }



    // 获取客户端
    @Before
    public   void getClient() throws Exception{
        Settings settings = Settings.builder()
                .put("cluster.name", "aubin-cluster").build();
        client =  new PreBuiltTransportClient(settings). addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.125.132"), 9300));
    }

    // 关闭客户端
    @After
    public  void closeClient(){
        if (this.client != null){
            this.client.close();
        }
    }
}
