package com.sxl.flink.project;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * 项目的生产者（消费者在scala的 project 包里）
 * Date: 2020/2/21 16:08
 * Author: sxl
 */
public class ProjectKafkaProducer {

    public static void main(String[] args) throws Exception{

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());


        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);

        String topic = "pktest";

        //通过死循环一直不停往kafka的broker中生产数据
        while (true){
            StringBuilder builder = new StringBuilder();

            builder.append("flink").append("\t")
                    .append("CN").append("\t")
                    .append(getLevel()).append("\t")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\t")
                    .append(getIps()).append("\t")
                    .append(getDomains()).append("\t")
                    .append(getTraffic()).append("\t");

            System.out.println(builder.toString());

            producer.send(new ProducerRecord<String, String>(topic,builder.toString()));

            Thread.sleep(2000);
        }
    }

    private static long getTraffic() {
        return new Random().nextInt(10000);
    }

    private static String getDomains() {
        String[] domains = new String[]{
                "v1.go2yd.com",
                "v2.go2yd.com",
                "4399.com",
                "avi.av1.com",
                "vim.go2yd.com"
        };
        return domains[new Random().nextInt(domains.length)];
    }

    private static String getIps() {
        String[] ips = new String[]{
                "223.104.18.110",
                "114.23.44.156",
                "123.45.67.21",
                "112.32.123.156",
                "175.12.44.198",
                "22.33.44.55",
                "11.127.133.156",
                "192.168.0.1",
                "116.23.24.25",
                "119.110.120.114"
        };
         return ips[new Random().nextInt(ips.length)];
    }

    //生产level数据
    public static  String getLevel(){
        String[]levels = new String[]{"M","E"};
        return levels[new Random().nextInt(levels.length)];
    }
}
