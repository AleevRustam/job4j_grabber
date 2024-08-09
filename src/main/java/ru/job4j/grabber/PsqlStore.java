package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO post (link, created) "
                + "VALUES (?, ?) "
                + "ON CONFLICT (link) DO UPDATE SET created = EXCLUDED.created RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, post.getLink());
            statement.setTimestamp(2, Timestamp.valueOf(post.getCreated()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post.setId(resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        try (InputStream input = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            if (input == null) {
                System.out.println("Unable to find rabbit.properties");
            }
            config.load(input);
            try (PsqlStore store = new PsqlStore(config)) {
                HabrCareerDateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
                HabrCareerParse parse = new HabrCareerParse(dateTimeParser);

                List<Post> posts = parse.list("https://career.habr.com");
                for (Post post : posts) {
                    store.save(post);
                }

                List<Post> savedPosts = store.getAll();
                System.out.println("All saved posts:");
                savedPosts.forEach(System.out::println);

                int exampleId = 2;
                Post postById = store.findById(exampleId);
                System.out.println("Post found by ID " + exampleId + " :");
                if (postById != null) {
                    System.out.println(postById);
                } else {
                    System.out.println("Post with ID " + exampleId + " not found.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
