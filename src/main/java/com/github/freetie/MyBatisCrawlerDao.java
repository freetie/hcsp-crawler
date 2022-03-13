package com.github.freetie;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisCrawlerDao {
    SqlSessionFactory sqlSessionFactory;

    public MyBatisCrawlerDao() throws IOException {
        String resource = "db/mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public void insertLink(String url) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.freetie.NewsMapper.insertLink", url);
        }
    }

    public synchronized String takeLink() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String url = session.selectOne("com.github.freetie.NewsMapper.takeLink");
            session.delete("com.github.freetie.NewsMapper.deleteLink", url);
            return url;
        }
    }

    public void insertNews(News news) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.freetie.NewsMapper.insertNews", news);
        }
    }

    public boolean isLinkProcessed(String url) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            int count = session.selectOne("com.github.freetie.NewsMapper.countNewsByUrl", url);
            return count != 0;
        }
    }
}

