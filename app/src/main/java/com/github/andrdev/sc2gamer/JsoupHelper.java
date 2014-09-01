package com.github.andrdev.sc2gamer;


import android.content.ContentValues;
import android.util.Log;

import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.NewsTable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


public class JsoupHelper {
    private static int sGamesPageCount = 0;
    private static int sGamesPageTotal = 1;
    private static int sNewsPageCount = 0;
    private static int sNewsPageTotal = 200;
    public static final String GAME_SITE = "http://www.gosugamers.net";
    private static final String USER_AGENT = "Mozilla";
    private static final String SCHEME = "http";
    private static final String NEWS = "news/archive";
    private static final String GAME = "dota2";
    private static final String GAMES_PAGES = "gosubet?u-page=";
    private static final DateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm z");

    public static String getArticle(String link) {
        String article = "";
        try {
            Document doc = Jsoup.connect(GAME_SITE + link).userAgent(USER_AGENT).get();
            Elements elements = doc.select("div[class=text clearfix").select("p");
            article = elements.html();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return article;
    }

    public static LinkedList<ContentValues> getNews() {
        LinkedList<ContentValues> newsList = new LinkedList<ContentValues>();
        for (int i = 0; i < 2; i++) {
            try {
                sNewsPageCount++;
                Document doc = Jsoup.connect(GAME_SITE + "/" + GAME + "/" + NEWS).userAgent(USER_AGENT).get();
                Elements elements = doc.select("table[class=simple gamelist medium")
                        .select("tbody").select("tr");
                ContentValues row;
                for (Element e : elements) {
                    row = new ContentValues();
                    row.put(NewsTable.TITLE, e.select("a").get(0).text()); //title
                    row.put(NewsTable.LINK, e.select("a").attr("href")); //link
                    newsList.add(row);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newsList;
    }

    public static LinkedList<String> getGamesLinks() {
        LinkedList<String> linkList = new LinkedList<String>();
        for (int i = 0; i < 2; i++) {
            Document doc = null;
            try {
                sGamesPageCount++;
                doc = Jsoup.connect(GAME_SITE + "/" + GAME + "/" + GAMES_PAGES + sGamesPageCount)
                        .userAgent(USER_AGENT).get();
                Elements elements = doc.select("div[class=box").get(1).select("div[class=pages").select("a");
                if (sGamesPageCount == sGamesPageTotal) {
                    String s = elements.last().attr("href");
                    sGamesPageTotal = Integer.valueOf(s.substring(s.lastIndexOf('=') + 1, s.length()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements elements = doc.select("div[class=box").get(1).select("tr");
            for (Element e : elements) {
                linkList.add(e.select("a").attr("href"));
            }
        }
        return linkList;
    }

    public static ContentValues getGameInfo(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(GAME_SITE + url).userAgent(USER_AGENT).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContentValues cv = new ContentValues();
        String logoLink1;
        String logoLink2;

        cv.put(GamesTable.TEAM1_NAME, doc.select("div[class=opponent opponent1")
                .select("a").text());
        logoLink1 = doc.select("div[class=opponent opponent1")
                .select("img").attr("src");
        cv.put(GamesTable.TEAM1_LOGO, logoLink1
                .substring(logoLink1.lastIndexOf("/") + 1, logoLink1.length()));
        cv.put(GamesTable.TEAM2_NAME, doc.select("div[class=opponent opponent2")
                .select("a").text());
        logoLink2 = doc.select("div[class=opponent opponent2")
                .select("img").attr("src");
        cv.put(GamesTable.TEAM2_LOGO, logoLink2
                .substring(logoLink2.lastIndexOf("/") + 1, logoLink2.length()));
        cv.put(GamesTable.TIME, parseTime(doc.select("p[class=datetime]").text()));
        return cv;
    }

    private static long parseTime(String time) {
        Date gameTime = new Date();
        try {
            gameTime = simpleDateFormat.parse(time + " CEST");
        } catch (ParseException e) {
            Log.e("JsoupHelper", "error parsing time", e);
        }
        return gameTime.getTime()/1000;
    }

    static public byte[] getPhoto(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static int getGamesPageCount() {
        return sGamesPageCount;
    }

    public static void setGamesPageCount(int gamesPageCount) {
        sGamesPageCount = gamesPageCount;
    }

    public static boolean isLastGamesPage() {
        return sGamesPageCount >= sGamesPageTotal;
    }

    public static int getNewsPageCount() {
        return sNewsPageCount;
    }

    public static void setNewsPageCount(int newsPageCount) {
        sNewsPageCount = newsPageCount;
    }

    public static int getsNewsPageTotal() {
        return sNewsPageTotal;
    }

    public static boolean isLastNewsPage() {
        return sNewsPageCount >= sNewsPageTotal;
    }


}



