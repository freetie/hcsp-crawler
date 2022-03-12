package com.github.freetie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class Crawler {
    MyBatisCrawlerDao dao = new MyBatisCrawlerDao();

    public Crawler() throws IOException {
    }

    public void run() throws IOException {
        String link;
        while ((link = dao.takeLink()) != null) {
            crawlPage(link);
        }
    }


    public void crawlPage(String link) throws IOException {
        System.out.println(link);
        Document doc = Jsoup.connect(link).get();
        crawlPageLinks(doc);
        Element header = doc.selectFirst("h1.main-title");
        Elements paragraphs = doc.select(".article p");
        if (header != null && paragraphs.size() > 0) {
            String content = paragraphs.stream().map(Element::text).collect(Collectors.joining("\n"));
            News news = new News(link, header.text(), content);
            dao.insertNews(news);
        }
        dao.deleteLink(link);
    }

    public void crawlPageLinks(Document doc) {
        Set<String> links = doc.select("a").stream().map(element -> element.attr("href")).collect(Collectors.toSet());
        for (String link :
                links) {
            if (!isValidNewsLink(link)) {
                continue;
            }
            if (dao.isLinkProcessed(link)) {
                continue;
            }
            if (link.startsWith("//")) {
                link = (doc.baseUri().startsWith("https:") ? "https:" : "http:") + link;
            }
            dao.insertLink(link);
        }
    }

    public boolean isValidNewsLink(String link) {
        return link != null && link.contains("news.sina.com.cn") && link.length() <= 255;
    }
}
