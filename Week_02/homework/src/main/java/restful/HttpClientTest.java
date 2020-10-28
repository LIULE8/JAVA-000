package restful;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {

  public static final String URL = "http://127.0.0.1:8803/";

  @SneakyThrows
  public static void main(String[] args) {
    CloseableHttpClient client = HttpClients.custom().build();
    HttpGet get = new HttpGet(URL);
    HttpResponse response = client.execute(get);
    String body = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    System.out.println(body);
  }
}
