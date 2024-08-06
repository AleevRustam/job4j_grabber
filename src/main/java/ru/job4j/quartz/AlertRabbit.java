package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {

        Properties properties = loadProperties();
        int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"))) {

            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDataMap data = new JobDataMap();
            data.put("store", store);
            data.put("connection", connection);

            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();

            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();

            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();

            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown(true);
            System.out.println(store);

        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(input);
            Class.forName(properties.getProperty("driver-class-name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());

            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rabbit (created_date) VALUES (CURRENT_TIMESTAMP)")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}