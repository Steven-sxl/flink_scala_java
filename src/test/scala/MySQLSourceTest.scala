import com.sxl.flink.project.MySQLSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

import scala.collection.mutable

/**
  *
  * 测试MySQLSource.scala 是否可用。
  * Date: 2020/2/25 14:16
  * Author: sxl
  */
object MySQLSourceTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val data = env.addSource(new MySQLSource).setParallelism(1)
    data.print()

    env.execute("MySQLSourceTest")

  }

}
