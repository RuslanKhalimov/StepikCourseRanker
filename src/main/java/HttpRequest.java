import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpRequest implements AutoCloseable {
    private final BufferedReader reader;
    private final HttpURLConnection connection;

    HttpRequest(String link, int readTimeout, int connectionTimeout) throws IOException {
        connection = (HttpURLConnection) new URL(link).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(readTimeout);
        connection.setReadTimeout(connectionTimeout);

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public BufferedReader getReader() {
        return reader;
    }

    @Override
    public void close() throws Exception {
        reader.close();
        connection.disconnect();
    }
}
