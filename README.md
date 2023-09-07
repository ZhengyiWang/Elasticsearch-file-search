# 使用Elasticsearch实现的一个对word，pdf，txt文件的全文内容检索

依赖版本  Elasticsearch-7.6.2
参考博客：[https://www.cnblogs.com/strongchenyu/p/13777596.html](https://www.cnblogs.com/strongchenyu/p/13777596.html)


部署流程
在ES的各台服务器上通过命令
./bin/elasticsearch-plugin install file:///usr/local/setup/elasticsearch-7.6.2/ingest-attachment-7.6.2.zip
安装 文本抽取插件


相似地，通过命令
./bin/elasticsearch-plugin install file:///usr/local/setup/elasticsearch-7.6.2/elasticsearch-analysis-ik-7.6.2.zip
安装ik分词器

在Kibana中执行
PUT /_ingest/pipeline/attachment
{
    "description": "Extract attachment information",
    "processors": [
        {
            "attachment": {
                "field": "content",
                "ignore_missing": true
            }
        },
        {
            "remove": {
                "field": "content"
            }
        }
    ]
}
定义文本抽取管道


打开`Test`目录，请运行前在Contant中修改变量为自己的变量

```java
final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(AuthScope.ANY,
		new UsernamePasswordCredentials(Contant.username, Contant.password));  //es账号密码

RestHighLevelClient client = new RestHighLevelClient(
		RestClient.builder(
				new HttpHost(Contant.HOSTNAME, Contant.PORT, Contant.SCHEME)
		).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				httpClientBuilder.disableAuthCaching();
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		})
);

ElasticOperation elo = new ElasticOperation();
elo.setClient(client);
```
这段代码实现带密码连接ES客户端



执行如下代码实现对应的增删改查功能
```
/*将对应目录下的文件上传至Elasticsearch服务器。*/
@Test
public void dirUploadTest() throws IOException {
    List<FileObj> files = fileOperation.readFileByDir(dirPath);
    ElasticOperation elo = eloFactory.generate();

    for (FileObj fileObj : files) {
        elo.upload(fileObj);
    }
}
```


```
/*根据搜索的分词结果搜索对应的文档*/
@Test
public void fileSearchTest() throws IOException {
    ElasticOperation elo = eloFactory.generate();

    elo.search("数据库国务院计算机网络");
}
```


```
/*根据传入的索引号删除索引*/
@Test
public void deleteindex() throws IOException{
	ElasticOperation elo = eloFactory.generate();

	elo.delete("fileindex","BhvCFooBicFwLRuXC8Ve");
}
```


```java
/*通过删除原始索引后再创建新的索引实现对索引的更新*/
@Test
public void updatecontent() throws IOException{
	ElasticOperation elo = eloFactory.generate();

	FileObj fileObj = fileOperation.readFile(path);
	elo.update("fileindex","LxvCFooBicFwLRuXaMXk",fileObj);
}
```

