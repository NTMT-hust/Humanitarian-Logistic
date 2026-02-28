package com.humanitarian.logistics.collector;

import com.google.gson.*;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSearchFetcher {

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String CX = "YOUR_CX";
    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws Exception {
        List<SearchResult> results = searchAndFetchFullContent(
                "bão Yagi after:2025-11-11 before:2025-12-11");

        for (SearchResult r : results) {
            System.out.println("===== RESULT =====");
            System.out.println("Title : " + r.title);
            System.out.println("URL   : " + r.url);
            System.out.println("Full Content:\n" + r.fullText);
            System.out.println();
        }
    }

    // ---------------------------
    // SEARCH + FETCH FULL CONTENT
    // ---------------------------
    public static List<SearchResult> searchAndFetchFullContent(String query) throws Exception {

        List<SearchResult> results = new ArrayList<>();

        String apiUrl = "https://www.googleapis.com/customsearch/v1?" +
                "key=" + API_KEY +
                "&cx=" + CX +
                "&q=" + java.net.URLEncoder.encode(query, "UTF-8") +
                "&num=10&start=1";

        Request request = new Request.Builder().url(apiUrl).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
            throw new IOException("Search API error: " + response.code());

        JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
        JsonArray items = json.getAsJsonArray("items");

        if (items == null)
            return results;

        for (JsonElement el : items) {
            JsonObject obj = el.getAsJsonObject();

            String title = obj.get("title").getAsString();
            String link = obj.get("link").getAsString();

            String fullContent = fetchPageContent(link);

            results.add(new SearchResult(title, link, fullContent));
        }

        return results;
    }

    // ---------------------------
    // FETCH FULL PAGE HTML + TEXT
    // ---------------------------
    public static String fetchPageContent(String url) {
        try {
            Request req = new Request.Builder().url(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            Response resp = client.newCall(req).execute();
            String html = resp.body().string();

            // Parse HTML into readable text
            Document doc = Jsoup.parse(html);

            // This extracts readable page content
            return doc.body().text();

        } catch (Exception e) {
            return "Error fetching content: " + e.getMessage();
        }
    }

    // ---------------------------
    // RESULT DATA CLASS
    // ---------------------------
    static class SearchResult {
        String title;
        String url;
        String fullText;

        public SearchResult(String title, String url, String fullText) {
            this.title = title;
            this.url = url;
            this.fullText = fullText;
        }
    }
}
