package cn.doitedu.dw.idmap

import cn.doitedu.commons.util.SparkUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}


/**
  * Created by ChenLongWen on 2020/4/22.
  */
object Demo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkUtils.getSparkSession(this.getClass.getSimpleName)
    import spark.implicits._
    val ds: Dataset[String] = spark.read.textFile("data/graphx/input/dehua.txt")


    val vertices: RDD[(Long, String)] = ds.rdd.flatMap(line => {
      val fields: Array[String] = line.split(",")

      //在spark的图计算API中,点需要表示成一个tuple ==> (点的唯一标识,点的数据)
      Array((fields(0).hashCode.toLong, fields(0))
        (fields(1).hashCode.toLong, fields(0)),
        (fields(2).hashCode.toLong, fields(0)),
      )

    })




  }
}
