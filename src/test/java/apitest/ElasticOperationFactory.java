package apitest;

import api.ElasticOperation;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ElasticOperationFactory {

    private List<ElasticOperation> eloList = new ArrayList<>();

    public ElasticOperation generate() {

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

        eloList.add(elo);

        return elo;
    }


    public void releaseAll() {
        for (ElasticOperation elo : eloList) {
            release(elo);
        }
    }

    public void release(ElasticOperation elo) {
        try {
            elo.getClient().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
