//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.elasticsearch.ElasticsearchException;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//public class ES5_6_5 {
//
//    private static Logger logger = LogManager.getLogger(ES5_6_5.class);
//    private String index = "indextest";
//    private String type = "blogtest";
//    private TransportClient client;
//    @Before
//    @SuppressWarnings({ "unchecked" })
//    public void before() throws UnknownHostException, InterruptedException, ExecutionException {
//        Settings esSettings =  Settings.builder()
//                .put("cluster.name", "elasticsearch") //设置ES实例的名称
//                .put("client.transport.sniff", true) //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
//                .build();
//        client = new PreBuiltTransportClient(esSettings);//初始化client较老版本发生了变化，此方法有几个重载方法，初始化插件等。
//
//
//
//        //此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
//
//        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//    }
//
//    // 创建一个索引
//    private void createIndex() {
//        try {
//            CreateIndexResponse indexResponse = client
//                    .admin()
//                    .indices()
//                    .prepareCreate(index)
//                    .get();
//
//            System.out.println(indexResponse.isAcknowledged()); // true表示创建成功
//        } catch (ElasticsearchException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //新增文档
//    public void createDoc(Map<String,Object> json ) {
//        IndexResponse response = client.prepareIndex(index,type).setSource(json).get();
//        logger.info("插入一条文档。。。。"+response.toString());
//    }
//
//    //字段查询文档
//    private void getByField( ) {
//        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("name", "wang qing");
//        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setExplain(true).setQuery(queryBuilder).get();
//        SearchHits searchHits = searchResponse.getHits();
//        System.out.println("SCORE.......................................................");
//        for (SearchHit searchHit: searchHits){
//            logger.info(searchHit.getExplanation());
//        }
//    }
//    //display所有文档
//    private void displayDoc(){
//        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
//        SearchResponse searchResponse = client.prepareSearch(index).setQuery(queryBuilder).get();
//        SearchHits searchHits = searchResponse.getHits();
//        System.out.println("输出所有的文档。。。。。。");
//        for (SearchHit searchHit: searchHits){
//            logger.info(searchHit.getSourceAsString());
//        }
//    }
//    @Test
//    public void TestAll() throws JsonProcessingException {
//
//        Map<String,Object> json1 = new HashMap<String, Object>();
//        json1.put("name","wang qing");
//        json1.put("age",21);
//        json1.put("content","she will send my birthday cake");
//
//        Map<String,Object> json2 = new HashMap<String, Object>();
//        json2.put("name","WANG qing QING");
//        json2.put("age",21);
//        json2.put("content","she will send my birthday cake");
//
//        Map<String,Object> json3 = new HashMap<String, Object>();
//        json3.put("name","wang qing");
//        json3.put("age",25);
//        json3.put("content","she will send my birthday cake");
//
//        Map<String,Object> json4 = new HashMap<String, Object>();
//        json4.put("name","zhang san");
//        json4.put("age",21);
//        json4.put("content","she will send my birthday cake");
//
//        Map<String,Object> json5 = new HashMap<String, Object>();
//        json5.put("name","qing wang");
//        json5.put("age",11);
//        json5.put("content","she will send my birthday cake");
//
//        Map<String,Object> json6 = new HashMap<String, Object>();
//        json6.put("name","wang qing QING");
//        json6.put("age",21);
//        json6.put("content","she will send my birthday cake");
////        createIndex();
//
////        createDoc(json1);
////        createDoc(json2);
////        createDoc(json3);
////        createDoc(json4);
////        createDoc(json5);
////        createDoc(json6);
//        displayDoc();
//        getByField();
//    }
//}
