package com.github.andrdev.sc2gamer;


import android.content.ContentValues;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


public class JsoupHelper {
    private static int sGamesPageCount = 0;
    private static int sGamesPageTotal = 1;
    private static int sNewsPageCount = 300;
    private static int sNewsPageTotal = 1;
    static final String GAME_SITE = "http://www.gosugamers.net";
    private static final String USER_AGENT="Mozilla";
    private static final String SCHEME = "http";
    private static final String NEWS = "news/archive";
    private static final String GAME = "dota2";
    private static final String GAMES_PAGES = "gosubet?u-page=";

    public static List getArticle(String link) {
        List<ContentValues> cv = new ArrayList<ContentValues>();
        ContentValues item = new ContentValues();
        try {
            Document doc = Jsoup.connect(GAME_SITE +link).userAgent(USER_AGENT).get();
            Elements elements = doc.select("div[class=text clearfix").select("p");
//            for (Element e : elements) {
//                e.select("img").remove().append("/n").prepend("/n");
//            }
            item.put("ARTICLE", elements.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
        cv.add(item);
        return cv;
    }

    public static  List<ContentValues> getNews() {
        List<ContentValues> newsList = new ArrayList<ContentValues>();
        for(int i=0;i<2;i++){
            try {
                sGamesPageCount++;
            Document doc = Jsoup.connect(GAME_SITE +"/"+GAME+"/"+NEWS).userAgent(USER_AGENT).get();
            Elements elements = doc.select("table[class=simple gamelist medium")
                    .select("tbody").select("tr");
            ContentValues row;
            for (Element e : elements) {
                row = new ContentValues();
                row.put(NewsTable.TITLE, e.select("a").get(0).text()); //title
                row.put(NewsTable.LINK, e.select("a").attr("href")); //mLink
//                Document doc2 = Jsoup.connect("http://www.gosugamers.net" + news.getLink()).userAgent("Mozilla").get();
//                news.setShortArticle(doc2.select("div[class=text clearfix").select("p").get(1).select("strong").text());
                newsList.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }}
        return newsList;
    }

    public static  List<ContentValues> getGames() {
        List<ContentValues> gameList = getGameInfo(getGamesLinks());
        return gameList;
    }

    private static List<String> getGamesLinks() {
        List<String> linkList = new ArrayList<String>();
        Document doc = null;
        for(int i=0;i<2;i++){
        try {
            sGamesPageCount++;
            doc = Jsoup.connect(GAME_SITE +"/"+ GAME + "/" + GAMES_PAGES + sGamesPageCount)
                    .userAgent(USER_AGENT).get();
            Elements e1 =  doc.select("div[class=box").get(1).select("div[class=pages").select("a");
            String s = e1.last().attr("href");
            if(sGamesPageCount==sGamesPageTotal){
                sGamesPageTotal = Integer.valueOf(s.substring(s.lastIndexOf('=')+1, s.length()));}
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements = doc.select("div[class=box").get(1).select("tr");
        for (Element e : elements) {
            linkList.add(e.select("a").attr("href"));
        }}
        return linkList;
    }

    private static List<ContentValues> getGameInfo(List<String> links) {
        List<ContentValues> gameList = new ArrayList<ContentValues>();
        if(links.isEmpty()) {
            return gameList;
        }
        for (String s : links) {
            Document doc = null;
            try {
                doc = Jsoup.connect(GAME_SITE+s).userAgent(USER_AGENT).get();
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
                    .substring(logoLink2.lastIndexOf("/")+1, logoLink2.length()));
            cv.put(GamesTable.TIME, parseTime(doc.select("p[class=datetime]").text()));
//            cv.put(GamesTable.TEAM1_NAME, doc.select("p[class=bestof]").text());
            gameList.add(cv);
        }
        return gameList;
    }

    private static long parseTime(String time) {
        DateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm z");
        Date gameTime = new Date();
            try {
                gameTime = simpleDateFormat.parse(time +" CEST");
            } catch (ParseException e) {
                Log.e("JsoupHelper", "error parsing time", e);
            }
        return gameTime.getTime();
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

    public static boolean isLastNewsPage() {
        return sNewsPageCount >= sNewsPageTotal;
    }


    private static class GetGameInfo implements Callable<List> {
        List<String> linkList;
        public GetGameInfo(List list) {
            this.linkList = list;
        }
        @Override
        public List<ContentValues> call() throws Exception {
            List <ContentValues> list2 = new ArrayList<ContentValues>();
            list2 = parseGamePageConc(linkList);
            return list2;
        }
    }

    private static List<ContentValues> getGameInfoConc(List<String> links) {
        int coreNum = Runtime.getRuntime().availableProcessors();
        int batch = links.size() / coreNum;
        ExecutorService executor = Executors.newFixedThreadPool(coreNum);
        List<Future> futureList = new ArrayList<Future>();
        int x = 1;

        do {
            futureList.add(executor.submit(new GetGameInfo(links.subList(batch * (x - 1), batch * x))));
            System.out.print(batch * (x - 1) + " " + batch * x);
            x++;
            if (x == coreNum) {
//                parseGamePageConc = futureList.add(executor.submit
//                        (new GetGameInfo(links.subList(batch * (x - 1), links.size()))));
                System.out.print(batch + " " + (batch * (x - 1)) + " " + links.size());
                x++;
            }
        } while (x < coreNum + 1);

        List<ContentValues> gameList = new ArrayList<ContentValues>();
        for (Future<ArrayList> f : futureList) {
            try {
                 gameList.addAll(f.get());
            } catch (InterruptedException e) {
                Log.e("Jsoup", "error interupted getGameInfo", e);
            } catch (ExecutionException e) {
                Log.e("Jsoup", "error retrieving from interupted getGameInfo", e);
            }
        }
        executor.shutdown();
        return gameList;
    }

    private static List <ContentValues> parseGamePageConc(List<String> list) {
        List <ContentValues> list2 = new ArrayList<ContentValues>();
        for (String s : list) {
            try {
                Document doc = Jsoup.connect(GAME_SITE + s).userAgent(USER_AGENT).timeout(10 * 10000).get();
                ContentValues cv = new ContentValues();
                cv.put(GamesTable.TEAM1_NAME, doc.select("div[class=opponent opponent1").select("a").text());
                Log.d("DREE-Jsoup", doc.select("div[class=opponent opponent1").select("a").text());

                cv.put(GamesTable.TEAM1_LOGO, doc.select("div[class=opponent opponent1").select("src").text());
                cv.put(GamesTable.TEAM2_NAME, doc.select("div[class=opponent opponent2").select("a").text());
                cv.put(GamesTable.TEAM2_LOGO, doc.select("div[class=opponent opponent2").select("src").text());
                cv.put(GamesTable.TIME, parseTime(doc.select("p[class=datetime]").text()));
//              cv.put(GamesTable.TEAM1_NAME, doc.select("p[class=bestof]").text());
                list2.add(cv);
            } catch (IOException ioe) {
                Log.e("Jsoup", "error parseGamePageConc", ioe);
              }
            }
            return list2;
        }
}



