# flink_scala_java
flink scala java 整合

# HotItems.java

  需求：每隔5分钟输出最近一小时内点击量最多的前 N 个商品。
  抽取出业务时间戳，告诉 Flink 框架基于业务时间做窗口

  过滤出点击行为数据

  按一小时的窗口大小，每5分钟统计一次，做滑动窗口聚合（Sliding Window）

  按每个窗口聚合，输出每个窗口中点击量前N名的商品
