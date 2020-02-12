import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * datastream 简单例子
 * 终端 输入： nc -lk 9999,然后执行该文件
 * Date: 2020/2/12 10:32
 * Author: sxl
 */
public class StreamTest {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        socketFunction(env);

        env.execute("StreamTest");
    }

    public static void socketFunction(StreamExecutionEnvironment env){
        DataStreamSource<String> data = env.socketTextStream("localhost",9999);
        data.print().setParallelism(1);
    }
}
