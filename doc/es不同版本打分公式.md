---
typora-copy-images-to: pic
typora-root-url: pic
---

## ElasticSearch使用

1. 从[这里](https://www.elastic.co/downloads/past-releases)下载，解压，打开bin目录下的`elasticsearch.bat`，启动ElasticSearch

   ![QQ截图20180118114420](/QQ截图20180118114420.png)

2. maven依赖

   ```java
           <dependency>
               <groupId>org.elasticsearch</groupId>
               <artifactId>elasticsearch</artifactId>
               <version>5.6.5</version>
           </dependency>
           <dependency>
               <groupId>org.elasticsearch.client</groupId>
               <artifactId>transport</artifactId>
               <version>5.6.5</version>
           </dependency>
   ```

   maven中的版本号要与下载的elasticsearch的版本号一致，且transport要与elasticsearch的版本号一致。

3. java连接elasticsearch

   ```java
       public void before() throws UnknownHostException, InterruptedException, ExecutionException {
           Settings esSettings =  Settings.settingsBuilder()
                   .put("cluster.name", "elasticsearch") //设置ES实例的名称
                   .put("client.transport.sniff", true) //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
                   .build();
           client = TransportClient.builder().settings(esSettings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
       }
   ```

   不同版本的elasticsearch的设置可能不同，上面的是2.4.6版本。

4. 创建index

   index就是数据库

   ```java
       // 创建一个索引
       private void createIndex() {
           try {
               CreateIndexResponse indexResponse = client
                       .admin()
                       .indices()
                       .prepareCreate(index)
                       .get();

               System.out.println(indexResponse.isAcknowledged()); // true表示创建成功
           } catch (ElasticsearchException e) {
               e.printStackTrace();
           }
       }
   ```

5. 插入数据

   ```java
       //新增文档
       public void createDoc(Map<String,Object> json ) {
           IndexResponse response = client.prepareIndex(index,type).setSource(json).get();
           logger.info("插入一条文档。。。。"+response.toString());
       }
   ```

6. 输出数据

   ```java
       //display所有文档
       private void displayDoc(){
           QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
           SearchResponse searchResponse = client.prepareSearch(index).setQuery(queryBuilder).get();
           SearchHits searchHits = searchResponse.getHits();
           System.out.println("输出所有的文档。。。。。。");
           for (SearchHit searchHit: searchHits){
               logger.info(searchHit.getSourceAsString());
           }
       }
   ```

7. 按照字段查询

   ```java
       //字段查询文档
       private void getByField( ) {
           QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("name", "wang qing");
           SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setExplain(true).setQuery(queryBuilder).get();
           SearchHits searchHits = searchResponse.getHits();
           System.out.println("SCORE.......................................................");
   //        for (SearchHit searchHit: searchHits){
   //            logger.info(searchHit.getExplanation());
   //        }
           logger.info(searchResponse);
       }
   ```

8. 数据

   ```java
          Map<String,Object> json1 = new HashMap<String, Object>();
           json1.put("name","wang qing");
           json1.put("age",21);
           json1.put("content","she will send my birthday cake");

           Map<String,Object> json2 = new HashMap<String, Object>();
           json2.put("name","WANG qing QING");
           json2.put("age",21);
           json2.put("content","she will send my birthday cake");

           Map<String,Object> json3 = new HashMap<String, Object>();
           json3.put("name","wang qing");
           json3.put("age",25);
           json3.put("content","she will send my birthday cake");

           Map<String,Object> json4 = new HashMap<String, Object>();
           json4.put("name","zhang san");
           json4.put("age",21);
           json4.put("content","she will send my birthday cake");

           Map<String,Object> json5 = new HashMap<String, Object>();
           json5.put("name","qing wang");
           json5.put("age",11);
           json5.put("content","she will send my birthday cake");

           Map<String,Object> json6 = new HashMap<String, Object>();
           json6.put("name","wang qing QING");
           json6.put("age",21);
           json6.put("content","she will send my birthday cake");
   ```

9. main函数

   ```java
   先调用createIndex();
   再添加数据createDoc(json1);
   最后搜索getByField();
   ```



## 数据库

|      name      | age  |            content             |
| :------------: | :--: | :----------------------------: |
|   wang qing    |  25  | she will send my birthday cake |
|   zhang san    |  21  | she will send my birthday cake |
|   wang qing    |  21  | she will send my birthday cake |
| WANG qing QING |  21  | she will send my birthday cake |
|   qing wang    |  11  | she will send my birthday cake |
| wang qing QING |  21  | she will send my birthday cake |

##### 查询

> query( "name" : "wang qing" )



## 版本6.1.1

##### score公式

$$
score = tf*idf
$$

- $IDF$
  $$
  \begin{align*}
  IDF&=log(1+\frac{docCount-docFreq+0.5}{docFreq+0.5}) \\
  &=log(\frac{docCount+1}{docFreq+0.5})
  \end{align*}
  $$

  > $docCount$是所有文档的数量，$docFreq$是匹配到的文档数量。$IDF$表征词汇在其他文档出现的次数。

- $TF$
  $$
  TF=\frac{TermFreq*(k_1+1)*avgFieldLen}{TermFreq*avgFieldLen+k_1*[avgFieldLen+b*(FieldLen-avgFieldLen)]}
  $$

  > $TermFreq$是词汇出现次数，$FieldLen$是词汇所在文档的长度，$avgFieldLen$是文档的平均长度，$k_1=1.2$，$b=0.75$。$TF$表征词汇的重要程度。
  >
  > 需要指出的是，该例子中的计算都是针对分片进行的，不是针对所有文档。
  >
  > `k1` :  This parameter controls how quickly an increase in term frequency results in term-frequency saturation. The default value is `1.2`. Lower values result in quicker saturation, and higher values in slower saturation.
  >
  > `b`:    This parameter controls how much effect field-length normalization should have. A value of `0.0`disables normalization completely, and a value of `1.0` normalizes fully. The default is `0.75`.



```java
输出所有的文档。。。。。。
[INFO ] 2018-01-16 17:34:35,189 ES6_1_1 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:34:35,189 ES6_1_1 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:34:35,189 ES6_1_1 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:34:35,189 ES6_1_1 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:34:35,189 ES6_1_1 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:34:35,189 ES6_1_1 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-16 17:34:35,396 ES6_1_1 - 0.5753642 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.5753642 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
    0.5753642 = idf(), sum of:
      0.2876821 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        1.0 = docFreq
        1.0 = docCount
      0.2876821 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        1.0 = docFreq
        1.0 = docCount
    1.0 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      2.0 = avgFieldLength
      2.0 = fieldLength

[INFO ] 2018-01-16 17:34:35,396 ES6_1_1 - 0.22949816 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.22949816 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
    0.21072103 = idf(), sum of:
      0.105360515 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        4.0 = docFreq
        4.0 = docCount
      0.105360515 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        4.0 = docFreq
        4.0 = docCount
    1.089109 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      2.5 = avgFieldLength
      2.0 = fieldLength

[INFO ] 2018-01-16 17:34:35,396 ES6_1_1 - 0.19478415 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.19478415 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
    0.21072103 = idf(), sum of:
      0.105360515 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        4.0 = docFreq
        4.0 = docCount
      0.105360515 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        4.0 = docFreq
        4.0 = docCount
    0.92436975 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      2.5 = avgFieldLength
      3.0 = fieldLength

[INFO ] 2018-01-16 17:34:35,396 ES6_1_1 - 0.19478415 = weight(name:"wang qing" in 2) [PerFieldSimilarity], result of:
  0.19478415 = score(doc=2,freq=1.0 = phraseFreq=1.0
), product of:
    0.21072103 = idf(), sum of:
      0.105360515 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        4.0 = docFreq
        4.0 = docCount
      0.105360515 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        4.0 = docFreq
        4.0 = docCount
    0.92436975 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      2.5 = avgFieldLength
      3.0 = fieldLength
```



## 版本5.6.5（打分与6.1.1大致相同）

> 需要指出的是，与6.1.1版本的差异在于$tfNorm$中$fieldLength$的计算。6.1.1版本中$fieldLength$即文本单词数量，而5.6.5版本的$fieldLength$需要将文本单词数量经过两次映射得到，具体看[这里](https://www.cnblogs.com/wangjiuyong/articles/7055724.html), 大致是这样映射：
>
> | 文本词汇数量 | fieldLength |
> | :----: | :---------: |
> |   2    |    2.56     |
> |   3    |      4      |
> |   4    |      4      |
> |   5    |  5.2244897  |
> |   6    |  7.111111   |
> |   7    |  7.111111   |
> |   8    |    10.24    |
> |   9    |    10.24    |
> |   10   |    16.0     |
>
> 

```java
输出所有的文档。。。。。。
[INFO ] 2018-01-16 17:28:00,036 ES5_6_5 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:28:00,036 ES5_6_5 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:28:00,036 ES5_6_5 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:28:00,036 ES5_6_5 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:28:00,036 ES5_6_5 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:28:00,036 ES5_6_5 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-16 17:28:00,137 ES5_6_5 - 0.51623213 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.51623213 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
    0.5753642 = idf(), sum of:
      0.2876821 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        1.0 = docFreq
        1.0 = docCount
      0.2876821 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        1.0 = docFreq
        1.0 = docCount
    0.89722675 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      2.0 = avgFieldLength
      2.56 = fieldLength

[INFO ] 2018-01-16 17:28:00,138 ES5_6_5 - 0.51623213 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.51623213 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
    0.5753642 = idf(), sum of:
      0.2876821 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        1.0 = docFreq
        1.0 = docCount
      0.2876821 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        1.0 = docFreq
        1.0 = docCount
    0.89722675 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      2.0 = avgFieldLength
      2.56 = fieldLength

[INFO ] 2018-01-16 17:28:00,138 ES5_6_5 - 0.32088596 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.32088596 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
    0.36464313 = idf(), sum of:
      0.18232156 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        2.0 = docFreq
        2.0 = docCount
      0.18232156 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        2.0 = docFreq
        2.0 = docCount
    0.88 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      3.0 = avgFieldLength
      4.0 = fieldLength

[INFO ] 2018-01-16 17:28:00,138 ES5_6_5 - 0.32088596 = weight(name:"wang qing" in 1) [PerFieldSimilarity], result of:
  0.32088596 = score(doc=1,freq=1.0 = phraseFreq=1.0
), product of:
    0.36464313 = idf(), sum of:
      0.18232156 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        2.0 = docFreq
        2.0 = docCount
      0.18232156 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        2.0 = docFreq
        2.0 = docCount
    0.88 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
      1.0 = phraseFreq=1.0
      1.2 = parameter k1
      0.75 = parameter b
      3.0 = avgFieldLength
      4.0 = fieldLength
```



## 版本5.0.1(与5.6.5版本多加了一个式子)

##### score公式

$$
score=tf*idf+clause*boost*queryNorm
$$

- clause

  https://www.elastic.co/guide/en/elasticsearch/guide/current/_boosting_query_clauses.html

- $queryNorm$

  ​

```java
输出所有的文档。。。。。。
[INFO ] 2018-01-16 17:16:34,880 ES5_0_1 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:16:34,880 ES5_0_1 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:16:34,881 ES5_0_1 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:16:34,881 ES5_0_1 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:16:34,881 ES5_0_1 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:16:34,881 ES5_0_1 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-16 17:16:34,969 ES5_0_1 - 0.5063205 = sum of:
  0.5063205 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
    0.5063205 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
      0.5753642 = idf(), sum of:
        0.2876821 = idf(docFreq=1, docCount=1)
        0.2876821 = idf(docFreq=1, docCount=1)
      0.88 = tfNorm, computed from:
        1.0 = phraseFreq=1.0
        1.2 = parameter k1
        0.75 = parameter b
        3.0 = avgFieldLength
        4.0 = fieldLength
  0.0 = match on required clause, product of:
    0.0 = # clause
    1.0 = _type:blogtest, product of:
      1.0 = boost
      1.0 = queryNorm

[INFO ] 2018-01-16 17:16:34,969 ES5_0_1 - 0.25685528 = sum of:
  0.25685528 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
    0.25685528 = score(doc=0,freq=1.0 = phraseFreq=1.0
), product of:
      0.26706278 = idf(), sum of:
        0.13353139 = idf(docFreq=3, docCount=3)
        0.13353139 = idf(docFreq=3, docCount=3)
      0.96177864 = tfNorm, computed from:
        1.0 = phraseFreq=1.0
        1.2 = parameter k1
        0.75 = parameter b
        2.3333333 = avgFieldLength
        2.56 = fieldLength
  0.0 = match on required clause, product of:
    0.0 = # clause
    1.0 = _type:blogtest, product of:
      1.0 = boost
      1.0 = queryNorm

[INFO ] 2018-01-16 17:16:34,969 ES5_0_1 - 0.25685528 = sum of:
  0.25685528 = weight(name:"wang qing" in 2) [PerFieldSimilarity], result of:
    0.25685528 = score(doc=2,freq=1.0 = phraseFreq=1.0
), product of:
      0.26706278 = idf(), sum of:
        0.13353139 = idf(docFreq=3, docCount=3)
        0.13353139 = idf(docFreq=3, docCount=3)
      0.96177864 = tfNorm, computed from:
        1.0 = phraseFreq=1.0
        1.2 = parameter k1
        0.75 = parameter b
        2.3333333 = avgFieldLength
        2.56 = fieldLength
  0.0 = match on required clause, product of:
    0.0 = # clause
    1.0 = _type:blogtest, product of:
      1.0 = boost
      1.0 = queryNorm

[INFO ] 2018-01-16 17:16:34,969 ES5_0_1 - 0.20667168 = sum of:
  0.20667168 = weight(name:"wang qing" in 1) [PerFieldSimilarity], result of:
    0.20667168 = score(doc=1,freq=1.0 = phraseFreq=1.0
), product of:
      0.26706278 = idf(), sum of:
        0.13353139 = idf(docFreq=3, docCount=3)
        0.13353139 = idf(docFreq=3, docCount=3)
      0.7738693 = tfNorm, computed from:
        1.0 = phraseFreq=1.0
        1.2 = parameter k1
        0.75 = parameter b
        2.3333333 = avgFieldLength
        4.0 = fieldLength
  0.0 = match on required clause, product of:
    0.0 = # clause
    1.0 = _type:blogtest, product of:
      1.0 = boost
      1.0 = queryNorm
```



## 版本2.4.6

##### 打分公式

$$
score=fieldNorm*fieldWeight*\sum_{word}IDF(word)+clause*boost*queryNorm
$$

- $IDF$
  $$
  IDF=1+log\frac{maxDoc}{docFreq+1}
  $$

- $fieldNorm$
  $$
  fieldNorm=\frac{1}{fieldLength}
  $$

  > 需要指出的是，$fieldLength$不是单纯的文本长度，而是将文本长度经过两次映射处理。比如文本长度是3个词汇，则$firldLength$是0.5。具体的映射算法可以看[这里](https://www.cnblogs.com/wangjiuyong/articles/7055724.html)。

- $fieldWeight$
  $$
  fieldWeight=termFreq
  $$




```java
输出所有的文档。。。。。。
[INFO ] 2018-01-16 17:38:19,972 ES2_4_6 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:38:19,974 ES2_4_6 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:38:19,974 ES2_4_6 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:38:19,974 ES2_4_6 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:38:19,974 ES2_4_6 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:38:19,974 ES2_4_6 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-16 17:38:20,003 ES2_4_6 - 1.25 = sum of:
  1.25 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
    1.25 = fieldWeight in 0, product of:
      1.0 = tf(freq=1.0), with freq of:
        1.0 = phraseFreq=1.0
      2.0 = idf(), sum of:
        1.0 = idf(docFreq=3, maxDocs=4)
        1.0 = idf(docFreq=3, maxDocs=4)
      0.625 = fieldNorm(doc=0)
  0.0 = match on required clause, product of:
    0.0 = # clause
    0.5 = _type:wqblogtest, product of:
      1.0 = boost
      0.5 = queryNorm

[INFO ] 2018-01-16 17:38:20,004 ES2_4_6 - 1.0 = sum of:
  1.0 = weight(name:"wang qing" in 3) [PerFieldSimilarity], result of:
    1.0 = fieldWeight in 3, product of:
      1.0 = tf(freq=1.0), with freq of:
        1.0 = phraseFreq=1.0
      2.0 = idf(), sum of:
        1.0 = idf(docFreq=3, maxDocs=4)
        1.0 = idf(docFreq=3, maxDocs=4)
      0.5 = fieldNorm(doc=3)
  0.0 = match on required clause, product of:
    0.0 = # clause
    0.5 = _type:wqblogtest, product of:
      1.0 = boost
      0.5 = queryNorm

[INFO ] 2018-01-16 17:38:20,004 ES2_4_6 - 0.38356602 = sum of:
  0.38356602 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
    0.38356602 = fieldWeight in 0, product of:
      1.0 = tf(freq=1.0), with freq of:
        1.0 = phraseFreq=1.0
      0.61370564 = idf(), sum of:
        0.30685282 = idf(docFreq=1, maxDocs=1)
        0.30685282 = idf(docFreq=1, maxDocs=1)
      0.625 = fieldNorm(doc=0)
  0.0 = match on required clause, product of:
    0.0 = # clause
    1.6294457 = _type:wqblogtest, product of:
      1.0 = boost
      1.6294457 = queryNorm

[INFO ] 2018-01-16 17:38:20,004 ES2_4_6 - 0.30685282 = sum of:
  0.30685282 = weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
    0.30685282 = fieldWeight in 0, product of:
      1.0 = tf(freq=1.0), with freq of:
        1.0 = phraseFreq=1.0
      0.61370564 = idf(), sum of:
        0.30685282 = idf(docFreq=1, maxDocs=1)
        0.30685282 = idf(docFreq=1, maxDocs=1)
      0.5 = fieldNorm(doc=0)
  0.0 = match on required clause, product of:
    0.0 = # clause
    1.6294457 = _type:wqblogtest, product of:
      1.0 = boost
      1.6294457 = queryNorm
```

## 版本2.0.1(公式与2.4.6一样)

```java
输出所有的文档。。。。。。
[INFO ] 2018-01-17 13:56:33,905 ElasticSearch2_0_1 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 13:56:33,908 ElasticSearch2_0_1 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 13:56:33,908 ElasticSearch2_0_1 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 13:56:33,909 ElasticSearch2_0_1 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 13:56:33,909 ElasticSearch2_0_1 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 13:56:33,909 ElasticSearch2_0_1 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-17 13:56:33,944 ElasticSearch2_0_1 - {
  "took" : 6,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 4,
    "max_score" : 1.0,
    "hits" : [ {
      "_shard" : 1,
      "_node" : "vWKEQxa6TmyJWEr5hYIY2w",
      "_index" : "wqindextest",
      "_type" : "wqblogtest",
      "_id" : "AWD-X8WIoe7TVbx45FWx",
      "_score" : 1.0,
      "_source":{"name":"wang qing QING","age":21,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 1.0,
        "description" : "sum of:",
        "details" : [ {
          "value" : 1.0,
          "description" : "weight(name:\"wang qing\" in 1) [PerFieldSimilarity], result of:",
          "details" : [ {
            "value" : 1.0,
            "description" : "fieldWeight in 1, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "tf(freq=1.0), with freq of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "phraseFreq=1.0",
                "details" : [ ]
              } ]
            }, {
              "value" : 2.0,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "idf(docFreq=1, maxDocs=2)",
                "details" : [ ]
              }, {
                "value" : 1.0,
                "description" : "idf(docFreq=1, maxDocs=2)",
                "details" : [ ]
              } ]
            }, {
              "value" : 0.5,
              "description" : "fieldNorm(doc=1)",
              "details" : [ ]
            } ]
          } ]
        }, {
          "value" : 0.0,
          "description" : "match on required clause, product of:",
          "details" : [ {
            "value" : 0.0,
            "description" : "# clause",
            "details" : [ ]
          }, {
            "value" : 0.5,
            "description" : "_type:wqblogtest, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "boost",
              "details" : [ ]
            }, {
              "value" : 0.5,
              "description" : "queryNorm",
              "details" : [ ]
            } ]
          } ]
        } ]
      }
    }, {
      "_shard" : 3,
      "_node" : "vWKEQxa6TmyJWEr5hYIY2w",
      "_index" : "wqindextest",
      "_type" : "wqblogtest",
      "_id" : "AWD-X8T5oe7TVbx45FWs",
      "_score" : 0.8903974,
      "_source":{"name":"wang qing","age":21,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.8903974,
        "description" : "sum of:",
        "details" : [ {
          "value" : 0.8903974,
          "description" : "weight(name:\"wang qing\" in 0) [PerFieldSimilarity], result of:",
          "details" : [ {
            "value" : 0.8903974,
            "description" : "fieldWeight in 0, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "tf(freq=1.0), with freq of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "phraseFreq=1.0",
                "details" : [ ]
              } ]
            }, {
              "value" : 1.4246359,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.71231794,
                "description" : "idf(docFreq=3, maxDocs=3)",
                "details" : [ ]
              }, {
                "value" : 0.71231794,
                "description" : "idf(docFreq=3, maxDocs=3)",
                "details" : [ ]
              } ]
            }, {
              "value" : 0.625,
              "description" : "fieldNorm(doc=0)",
              "details" : [ ]
            } ]
          } ]
        }, {
          "value" : 0.0,
          "description" : "match on required clause, product of:",
          "details" : [ {
            "value" : 0.0,
            "description" : "# clause",
            "details" : [ ]
          }, {
            "value" : 0.70193374,
            "description" : "_type:wqblogtest, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "boost",
              "details" : [ ]
            }, {
              "value" : 0.70193374,
              "description" : "queryNorm",
              "details" : [ ]
            } ]
          } ]
        } ]
      }
    }, {
      "_shard" : 3,
      "_node" : "vWKEQxa6TmyJWEr5hYIY2w",
      "_index" : "wqindextest",
      "_type" : "wqblogtest",
      "_id" : "AWD-X8VZoe7TVbx45FWu",
      "_score" : 0.8903974,
      "_source":{"name":"wang qing","age":25,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.8903974,
        "description" : "sum of:",
        "details" : [ {
          "value" : 0.8903974,
          "description" : "weight(name:\"wang qing\" in 2) [PerFieldSimilarity], result of:",
          "details" : [ {
            "value" : 0.8903974,
            "description" : "fieldWeight in 2, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "tf(freq=1.0), with freq of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "phraseFreq=1.0",
                "details" : [ ]
              } ]
            }, {
              "value" : 1.4246359,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.71231794,
                "description" : "idf(docFreq=3, maxDocs=3)",
                "details" : [ ]
              }, {
                "value" : 0.71231794,
                "description" : "idf(docFreq=3, maxDocs=3)",
                "details" : [ ]
              } ]
            }, {
              "value" : 0.625,
              "description" : "fieldNorm(doc=2)",
              "details" : [ ]
            } ]
          } ]
        }, {
          "value" : 0.0,
          "description" : "match on required clause, product of:",
          "details" : [ {
            "value" : 0.0,
            "description" : "# clause",
            "details" : [ ]
          }, {
            "value" : 0.70193374,
            "description" : "_type:wqblogtest, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "boost",
              "details" : [ ]
            }, {
              "value" : 0.70193374,
              "description" : "queryNorm",
              "details" : [ ]
            } ]
          } ]
        } ]
      }
    }, {
      "_shard" : 3,
      "_node" : "vWKEQxa6TmyJWEr5hYIY2w",
      "_index" : "wqindextest",
      "_type" : "wqblogtest",
      "_id" : "AWD-X8VNoe7TVbx45FWt",
      "_score" : 0.71231794,
      "_source":{"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.71231794,
        "description" : "sum of:",
        "details" : [ {
          "value" : 0.71231794,
          "description" : "weight(name:\"wang qing\" in 1) [PerFieldSimilarity], result of:",
          "details" : [ {
            "value" : 0.71231794,
            "description" : "fieldWeight in 1, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "tf(freq=1.0), with freq of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "phraseFreq=1.0",
                "details" : [ ]
              } ]
            }, {
              "value" : 1.4246359,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.71231794,
                "description" : "idf(docFreq=3, maxDocs=3)",
                "details" : [ ]
              }, {
                "value" : 0.71231794,
                "description" : "idf(docFreq=3, maxDocs=3)",
                "details" : [ ]
              } ]
            }, {
              "value" : 0.5,
              "description" : "fieldNorm(doc=1)",
              "details" : [ ]
            } ]
          } ]
        }, {
          "value" : 0.0,
          "description" : "match on required clause, product of:",
          "details" : [ {
            "value" : 0.0,
            "description" : "# clause",
            "details" : [ ]
          }, {
            "value" : 0.70193374,
            "description" : "_type:wqblogtest, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "boost",
              "details" : [ ]
            }, {
              "value" : 0.70193374,
              "description" : "queryNorm",
              "details" : [ ]
            } ]
          } ]
        } ]
      }
    } ]
  }
}
```





## 版本1.7.6

##### score

$$
\begin{align*}
score&=[queryWeight]+[fieldWeight]\\
&=[queryNorm*\sum_{word}{IDF(word)}]*[fieldNorm*TF*\sum_{word}IDF(word)]\\
\end{align*}
$$

- $queryNorm$
  $$
  queryNorm=\frac{1}{\sqrt{\sum_{word}IDF^2(word)}}
  $$

- $IDF$
  $$
  IDF=1+log\frac{maxDoc}{docFreq+1}
  $$

  > 在本例子中，$macDoc$是每个分片上的文档数量。

- $TF$
  $$
  TF=\sqrt{termFreq}
  $$

- $fieldNorm$
  $$
  fieldNorm=\frac{1}{fieldLength}
  $$

  > 需要指出的是，$fieldLength$不是单纯的文本长度，而是将文本长度经过两次映射处理。比如文本长度是3个词汇，则$firldLength$是4。具体的映射算法可以看[这里](https://www.cnblogs.com/wangjiuyong/articles/7055724.html)。
  >
  > ​

```java
输出所有的文档。。。。。。
[INFO ] 2018-01-16 17:42:31,308 ES1_7_6 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:42:31,310 ES1_7_6 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:42:31,310 ES1_7_6 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:42:31,310 ES1_7_6 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:42:31,310 ES1_7_6 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-16 17:42:31,310 ES1_7_6 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-16 17:42:31,340 ES1_7_6 - 0.74316853 = (MATCH) weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.74316853 = score(doc=0,freq=1.0), product of:
    0.99999994 = queryWeight, product of:
      1.1890697 = idf(), sum of:
        0.5945349 = idf(docFreq=2, maxDocs=2)
        0.5945349 = idf(docFreq=2, maxDocs=2)
      0.8409935 = queryNorm
    0.7431686 = fieldWeight in 0, product of:
      1.0 = tf(freq=1.0), with freq of:
        1.0 = phraseFreq=1.0
      1.1890697 = idf(), sum of:
        0.5945349 = idf(docFreq=2, maxDocs=2)
        0.5945349 = idf(docFreq=2, maxDocs=2)
      0.625 = fieldNorm(doc=0)

[INFO ] 2018-01-16 17:42:31,340 ES1_7_6 - 0.5945348 = (MATCH) weight(name:"wang qing" in 1) [PerFieldSimilarity], result of:
  0.5945348 = score(doc=1,freq=1.0), product of:
    0.99999994 = queryWeight, product of:
      1.1890697 = idf(), sum of:
        0.5945349 = idf(docFreq=2, maxDocs=2)
        0.5945349 = idf(docFreq=2, maxDocs=2)
      0.8409935 = queryNorm
    0.5945349 = fieldWeight in 1, product of:
      1.0 = tf(freq=1.0), with freq of:
        1.0 = phraseFreq=1.0
      1.1890697 = idf(), sum of:
        0.5945349 = idf(docFreq=2, maxDocs=2)
        0.5945349 = idf(docFreq=2, maxDocs=2)
      0.5 = fieldNorm(doc=1)

[INFO ] 2018-01-16 17:42:31,340 ES1_7_6 - 0.38356602 = (MATCH) weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.38356602 = fieldWeight in 0, product of:
    1.0 = tf(freq=1.0), with freq of:
      1.0 = phraseFreq=1.0
    0.61370564 = idf(), sum of:
      0.30685282 = idf(docFreq=1, maxDocs=1)
      0.30685282 = idf(docFreq=1, maxDocs=1)
    0.625 = fieldNorm(doc=0)

[INFO ] 2018-01-16 17:42:31,340 ES1_7_6 - 0.30685282 = (MATCH) weight(name:"wang qing" in 0) [PerFieldSimilarity], result of:
  0.30685282 = fieldWeight in 0, product of:
    1.0 = tf(freq=1.0), with freq of:
      1.0 = phraseFreq=1.0
    0.61370564 = idf(), sum of:
      0.30685282 = idf(docFreq=1, maxDocs=1)
      0.30685282 = idf(docFreq=1, maxDocs=1)
    0.5 = fieldNorm(doc=0)
```



## 版本1.0.1(打分与版本1.7.6一样)

```java
输出所有的文档。。。。。。
[INFO ] 2018-01-17 09:46:29,900 ES1_0_1 - {"name":"zhang san","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 09:46:29,902 ES1_0_1 - {"name":"qing wang","age":11,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 09:46:29,902 ES1_0_1 - {"name":"wang qing","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 09:46:29,902 ES1_0_1 - {"name":"wang qing","age":25,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 09:46:29,902 ES1_0_1 - {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"}
[INFO ] 2018-01-17 09:46:29,902 ES1_0_1 - {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"}
SCORE.......................................................
[INFO ] 2018-01-17 09:46:29,923 ES1_0_1 - {
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 4,
    "max_score" : 0.74316853,
    "hits" : [ {
      "_shard" : 3,
      "_node" : "FMhxjttLS6ORKSQw_EAc1w",
      "_index" : "indextest",
      "_type" : "blogtest",
      "_id" : "8TX_O5O9TBiJ80wTIxECYQ",
      "_score" : 0.74316853, "_source" : {"name":"wang qing","age":25,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.74316853,
        "description" : "weight(name:\"wang qing\" in 0) [PerFieldSimilarity], result of:",
        "details" : [ {
          "value" : 0.74316853,
          "description" : "score(doc=0,freq=1.0 = phraseFreq=1.0\n), product of:",
          "details" : [ {
            "value" : 0.99999994,
            "description" : "queryWeight, product of:",
            "details" : [ {
              "value" : 1.1890697,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              }, {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              } ]
            }, {
              "value" : 0.8409935,
              "description" : "queryNorm"
            } ]
          }, {
            "value" : 0.7431686,
            "description" : "fieldWeight in 0, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "tf(freq=1.0), with freq of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "phraseFreq=1.0"
              } ]
            }, {
              "value" : 1.1890697,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              }, {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              } ]
            }, {
              "value" : 0.625,
              "description" : "fieldNorm(doc=0)"
            } ]
          } ]
        } ]
      }
    }, {
      "_shard" : 3,
      "_node" : "FMhxjttLS6ORKSQw_EAc1w",
      "_index" : "indextest",
      "_type" : "blogtest",
      "_id" : "RpwQWfvHQzeypAzWd8RHfw",
      "_score" : 0.5945348, "_source" : {"name":"wang qing QING","age":21,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.5945348,
        "description" : "weight(name:\"wang qing\" in 1) [PerFieldSimilarity], result of:",
        "details" : [ {
          "value" : 0.5945348,
          "description" : "score(doc=1,freq=1.0 = phraseFreq=1.0\n), product of:",
          "details" : [ {
            "value" : 0.99999994,
            "description" : "queryWeight, product of:",
            "details" : [ {
              "value" : 1.1890697,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              }, {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              } ]
            }, {
              "value" : 0.8409935,
              "description" : "queryNorm"
            } ]
          }, {
            "value" : 0.5945349,
            "description" : "fieldWeight in 1, product of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "tf(freq=1.0), with freq of:",
              "details" : [ {
                "value" : 1.0,
                "description" : "phraseFreq=1.0"
              } ]
            }, {
              "value" : 1.1890697,
              "description" : "idf(), sum of:",
              "details" : [ {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              }, {
                "value" : 0.5945349,
                "description" : "idf(docFreq=2, maxDocs=2)"
              } ]
            }, {
              "value" : 0.5,
              "description" : "fieldNorm(doc=1)"
            } ]
          } ]
        } ]
      }
    }, {
      "_shard" : 0,
      "_node" : "FMhxjttLS6ORKSQw_EAc1w",
      "_index" : "indextest",
      "_type" : "blogtest",
      "_id" : "n7DXBp1XSNi47GXhxd7bZQ",
      "_score" : 0.38356602, "_source" : {"name":"wang qing","age":21,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.38356602,
        "description" : "weight(name:\"wang qing\" in 0) [PerFieldSimilarity], result of:",
        "details" : [ {
          "value" : 0.38356602,
          "description" : "fieldWeight in 0, product of:",
          "details" : [ {
            "value" : 1.0,
            "description" : "tf(freq=1.0), with freq of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "phraseFreq=1.0"
            } ]
          }, {
            "value" : 0.61370564,
            "description" : "idf(), sum of:",
            "details" : [ {
              "value" : 0.30685282,
              "description" : "idf(docFreq=1, maxDocs=1)"
            }, {
              "value" : 0.30685282,
              "description" : "idf(docFreq=1, maxDocs=1)"
            } ]
          }, {
            "value" : 0.625,
            "description" : "fieldNorm(doc=0)"
          } ]
        } ]
      }
    }, {
      "_shard" : 2,
      "_node" : "FMhxjttLS6ORKSQw_EAc1w",
      "_index" : "indextest",
      "_type" : "blogtest",
      "_id" : "vl_lzLcjRUqmBKgUp_SK5w",
      "_score" : 0.30685282, "_source" : {"name":"WANG qing QING","age":21,"content":"she will send my birthday cake"},
      "_explanation" : {
        "value" : 0.30685282,
        "description" : "weight(name:\"wang qing\" in 0) [PerFieldSimilarity], result of:",
        "details" : [ {
          "value" : 0.30685282,
          "description" : "fieldWeight in 0, product of:",
          "details" : [ {
            "value" : 1.0,
            "description" : "tf(freq=1.0), with freq of:",
            "details" : [ {
              "value" : 1.0,
              "description" : "phraseFreq=1.0"
            } ]
          }, {
            "value" : 0.61370564,
            "description" : "idf(), sum of:",
            "details" : [ {
              "value" : 0.30685282,
              "description" : "idf(docFreq=1, maxDocs=1)"
            }, {
              "value" : 0.30685282,
              "description" : "idf(docFreq=1, maxDocs=1)"
            } ]
          }, {
            "value" : 0.5,
            "description" : "fieldNorm(doc=0)"
          } ]
        } ]
      }
    } ]
  }
}
```



