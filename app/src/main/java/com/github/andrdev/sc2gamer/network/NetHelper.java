package com.github.andrdev.sc2gamer.network;


import android.content.ContentValues;
import android.util.Log;

import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.NewsTable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;


public class NetHelper {
    private static int sGamesPageCount = 0;
    private static int sGamesPageTotal = 1;
    private static int sNewsPageCount = 0;
    private static int sNewsPageTotal = 1;
    public static final String GAME_SITE = "http://www.gosugamers.net";
    private static final String USER_AGENT = "Mozilla";
    private static final String NEWS = "news/archive";
    private static final String GAME = "dota2";
    private static final String GAMES_PAGES = "gosubet?u-page=";
    private static final SimpleDateFormat mSimpleDateFormat =
            new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm z", Locale.UK);


    public static LinkedList<ContentValues> getNews() {
        LinkedList<ContentValues> newsList = new LinkedList<ContentValues>();
        for (int i = 0; i < 2; i++) {
            try {
                sNewsPageCount++;
                Document doc = Jsoup.connect(GAME_SITE + "/" + GAME + "/" + NEWS).userAgent(USER_AGENT).get();
                if (sNewsPageCount == sNewsPageTotal) {
                    Elements pagesElements = doc.select("div.pages").select("a");
                    String lastPageLink = pagesElements.last().attr("href");
                    sNewsPageTotal = getLastPageNumber(lastPageLink);
                }
                Elements elements = doc.select("table.simple.gamelist.medium")
                        .select("tbody").select("tr");
                for (Element e : elements) {
                    ContentValues row = new ContentValues();
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
                if (sGamesPageCount == sGamesPageTotal) {
                    Elements elements = doc.select("div.box").get(1).select("div.pages").select("a");
                    String lastPageLink = elements.last().attr("href");
                    sGamesPageTotal = getLastPageNumber(lastPageLink);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements elements = doc.select("div.box").get(1).select("tr");
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

        cv.put(GamesTable.TEAM1_NAME, doc.select("div.opponent.opponent1")
                .select("a").text());
        logoLink1 = doc.select("div.opponent.opponent1")
                .select("img").attr("src");
        cv.put(GamesTable.TEAM1_LOGO, logoLink1
                .substring(logoLink1.lastIndexOf("/") + 1, logoLink1.length()));
        cv.put(GamesTable.TEAM2_NAME, doc.select("div.opponent.opponent2")
                .select("a").text());
        logoLink2 = doc.select("div.opponent.opponent2")
                .select("img").attr("src");
        cv.put(GamesTable.TEAM2_LOGO, logoLink2
                .substring(logoLink2.lastIndexOf("/") + 1, logoLink2.length()));
        cv.put(GamesTable.TIME, parseTime(doc.select("p.datetime").text()));
        return cv;
    }

    private static int getLastPageNumber(String pageLink) {
        int pageNumber = Integer.valueOf
                (pageLink.substring(pageLink.lastIndexOf('=') + 1, pageLink.length()));
        return pageNumber;
    }

    private static long parseTime(String time) {
        Date gameTime = new Date();
        try {
            gameTime = mSimpleDateFormat.parse(time + " CEST");
        } catch (ParseException e) {
            Log.e("NetHelper", "error parsing time", e);
        }
        return gameTime.getTime() / 1000;
    }

    public static byte[] getTeamLogo(String urlSpec) throws IOException {
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

    public static boolean haveGamesPages() {
        return sGamesPageCount <= sGamesPageTotal;
    }

    public static int getNewsPageCount() {
        return sNewsPageCount;
    }

    public static void setNewsPageCount(int newsPageCount) {
        sNewsPageCount = newsPageCount;
    }

    public static boolean haveNewsPages() {
        return sNewsPageCount <= sNewsPageTotal;
    }

    public static ArrayList getArticle(String newsUrl) throws IOException {
        Document doc = Jsoup.connect(GAME_SITE+newsUrl).userAgent(USER_AGENT)
                .get();
        deleteTags(doc);
        String cssStyleLinks = headerCss(doc);
        String newsBody = newsBody(doc);
        ArrayList<String> str = new ArrayList<String>();
        str.add(cssStyleLinks);
        str.add(newsBody);
        return str;
    }
    private static void deleteTags(Document doc) {
        doc.select("div#posted-by").remove();
        doc.select("div.article-author").remove();
        doc.select("form.rating").remove();
    }
    private static String headerCss(Document doc) {
        return doc.head().select("base[href]").outerHtml().concat(
                doc.head().select("link[type=text/css]").outerHtml());
    }
    private static String newsBody(Document doc) {
        String body = doc.select("body.body-dota2")
                .select("div.columns")
                .select("div#col1.rows")
                .select("div#article.box")
                .select("div.content.light")
                .select("div.text.clearfix").html();
        return fixImagesUrl(body);
    }
    private static String fixImagesUrl(String body) {
        return body.replaceAll("src=\"/", String.format("src=\"%s", GAME_SITE));
    }
    private static String generateHtmlPage(String headerCss, String newsBody) throws IOException {
        BufferedReader htmlTemplate = new BufferedReader(new FileReader("file:///android_asset/templ.html"));
        StringBuilder sb = new StringBuilder();
        String temp = null;
        while ((temp = htmlTemplate.readLine()) != null) {
            sb.append(temp);
        }
        String finalPage = sb.toString();
        finalPage = finalPage.replace("xxxHEADxxx", headerCss);
        finalPage = finalPage.replace("xxxBODYxxx", newsBody);
        return finalPage;
    }

}



