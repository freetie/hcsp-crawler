package com.github.freetie;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        MyBatisCrawlerDao dao = new MyBatisCrawlerDao();
        for (int i = 0; i < 4; i++) {
            new Crawler(dao).start();
        }
    }
}
