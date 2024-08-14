package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private Parse parse;
    private Store store;
    private Scheduler scheduler;
    private int time;
    private Properties cfg;

    public Grabber() {

    }

    public Grabber(Parse parse, Store store, Scheduler scheduler, int time) {
        this.parse = parse;
        this.store = store;
        this.scheduler = scheduler;
        this.time = time;
    }

    public void cfg() throws IOException {
        cfg = new Properties();
        try (InputStream input = Grabber.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
                throw new FileNotFoundException("app.properties file not found");
            }
            cfg.load(input);
        }
        this.time = Integer.parseInt(cfg.getProperty("time"));
    }

    public Scheduler scheduler() throws SchedulerException {
        if (scheduler == null) {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        }
        return scheduler;
    }

    public Store store() {
        if (store == null) {
            store = new PsqlStore(cfg);
        }
        return store;
    }

    @Override
    public void init() throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(time)
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream();
                         OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                         BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

                        bufferedWriter.write("HTTP/1.1 200 OK\r\n");
                        bufferedWriter.write("Content-Type: text/html; charset=UTF-8\r\n\r\n");

                        bufferedWriter.write("<html><body><pre>\n");

                        for (Post post : store.getAll()) {
                            bufferedWriter.write("id : " + post.getId() + "<br>\n");
                            bufferedWriter.write("title : " + post.getTitle() + "<br>\n");
                            bufferedWriter.write("link : " + post.getLink() + "<br>\n");
                            bufferedWriter.write("description : " + post.getDescription() + "<br>\n");
                            bufferedWriter.write("created : " + post.getCreated() + "<br>\n");
                            bufferedWriter.write("<br>\n");
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        this.parse = parse;
        this.store = store;
        this.scheduler = scheduler;
        init();
    }

  public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> posts = parse.list("https://career.habr.com");
            for (Post post : posts) {
                store.save(post);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
        grab.web(store);
    }
}
