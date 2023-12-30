import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class preprocessing {

    private static final int THREAD_POOL_SIZE = 4; // Puedes ajustar esto según tus recursos

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        List<Future<List<String>>> futures = new ArrayList<>();
        String[] documentPaths = {"path/to/doc1.txt", "path/to/doc2.txt"}; // Tus rutas a documentos

        for (String path : documentPaths) {
            Future<List<String>> future = executor.submit(() -> {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(path)));
                    return tokenizeAndStem(content);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            futures.add(future);
        }

        // Recoger y procesar los resultados
        for (Future<List<String>> future : futures) {
            try {
                List<String> tokens = future.get();
                // Hacer algo con los tokens
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    private static List<String> tokenizeAndStem(String content) throws IOException {
        List<String> tokens = new ArrayList<>();
        try (Analyzer analyzer = new EnglishAnalyzer();
             TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(content))) {

            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                tokens.add(attr.toString());
            }

            tokenStream.end();
        }
        return tokens;
    }
}
