package ilsiya.sabirzyanova.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import ilsiya.sabirzyanova.model.QuestionsPage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class StackExchangeQueryService {
    private static StackExchangeQueryService instance;
    private String queryUrl;
    private int page = 1;

    private StackExchangeQueryService() {
    }

    public static StackExchangeQueryService getInstance() {
        if (instance == null) {
            instance = new StackExchangeQueryService();
        }
        return instance;
    }

    public QuestionsPage getQuestionsPage(String searchText, int pageNum) throws Exception {
        page = pageNum;
        HttpURLConnection connection = null;
        try {
            connection = openConnection(buildApiUrl(searchText));
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("response code: " + connection.getResponseCode());
            }
            InputStream is = getWrappedInputStream(connection.getInputStream(),
                    "gzip".equalsIgnoreCase(connection
                            .getContentEncoding()));
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }
            String ss = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionsPage resultQuestionsPage = objectMapper.readValue(ss, QuestionsPage.class);
            resultQuestionsPage.setPageNum(pageNum);
            return resultQuestionsPage;
        } finally {
            closeConnection(connection);
        }
    }

    private String buildApiUrl(String searchText) {
        StringBuilder url;
        if (queryUrl == null) {
            url = new StringBuilder("http://api.stackexchange.com/2.2/search/advanced?site=stackoverflow");
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("order", "desc");
            requestHeaders.put("sort", "activity");
            requestHeaders.put("intitle", "java");
            requestHeaders.put("filter", "default");
            requestHeaders.put("pagesize", "100");
            for (String headerName : requestHeaders.keySet()) {
                url.append("&").append(headerName).append("=").append(requestHeaders.get(headerName));
                queryUrl = url.toString();
            }
        } else {
            url = new StringBuilder(queryUrl);
        }
        url.append("&").append("q").append("=").append(searchText.replace(" ", "%20"));
        url.append("&").append("page").append("=").append(page);
        return url.toString();
    }

    private HttpURLConnection openConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        return connection;
    }

    private void closeConnection(HttpURLConnection connection) {
        try {
            if (connection != null) {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream getWrappedInputStream(InputStream is, boolean gzip)
            throws IOException {
        if (gzip) {
            return new BufferedInputStream(new GZIPInputStream(is));
        } else {
            return new BufferedInputStream(is);
        }
    }
}
