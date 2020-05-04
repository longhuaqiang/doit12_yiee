package cn.doitedu.dw.idmap

import cn.doitedu.commons.util.SparkUtils
import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by ChenLongWen on 2020/4/22.
  */
object Demo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkUtils.getSparkSession(this.getClass.getSimpleName)
    import spark.implicits._

    //加载原始数据
    val ds: Dataset[String] = spark.read.textFile("data/graphx/input/dehua.txt")


    //构造一个点rdd集合
    val vertices: RDD[(Long, String)] = ds.rdd.flatMap(line => {

      val fields: Array[String] = line.split(",")

      //在spark的图计算api中,点需要表示成一个tuple==> (点的唯一标识,点的数据)
      for (ele <- fields if StringUtils.isNotBlank(ele)) yield (ele.hashCode.toLong, ele)


      //      Array(
      //        (fields(0).hashCode.toLong, fields(0)),
      //        (fields(1).hashCode.toLong, fields(1)),
      //        (fields(2).hashCode.toLong, fields(2))
      //      )
    })

    //构造一个边rdd
    //spark graphx中对边的描述结构:   Edge(起始点id,目标点id,边数据)
    val edges: RDD[Edge[String]] = ds.rdd.flatMap(line => {
      val fields: Array[String] = line.split(",")
      /* val lst = new ListBuffer[Edge[String]]()
       for (i <- 0 to fields.length - 2) {
         val edge1: Edge[String] = Edge(fields(i).hashCode.toLong, fields(i + 1).hashCode.toLong, "")
         lst += edge1
       }
       lst*/

      for (i <- 0 to fields.length - 2 if StringUtils.isNotBlank(fields(i))) yield Edge(fields(i).hashCode.toLong, fields(i + 1).hashCode.toLong, "")

    })

    //用点集合 和 边集合构造一张图
    val graph: Graph[String, String] = Graph(vertices, edges)
    //调用图的算法: 连通子图算法
    val graph2: Graph[VertexId, String] = graph.connectedComponents()
    //从结果图中,取出图的点集合,即可得到我们想要的分组结果
    val vertices2: VertexRDD[VertexId] = graph2.vertices

    //(-1095633001,-1095633001)
    //(29003441,-1095633001)
    //(113568560,-774338670)
    //(1567005,-774338670)
    //(113568358,-1095633001)
    //(0,-774338670)
    //(681286,-774338670)
    //(-774337709,-1095633001)
    //(20977295,-774338670)
    //(1571810,-1095633001)
    //    vertices2.take(10).foreach(println)

    //将上面得到的映射关系rdd,收集到Driver端
    val idmpMap: collection.Map[VertexId, VertexId] = vertices2.collectAsMap()
    //然后作为广播变量出去
    val sc: SparkContext = spark.sparkContext
    val bc: Broadcast[collection.Map[VertexId, VertexId]] = sc.broadcast(idmpMap)


    //利用这个映射关系结果,来加工我们的原始数据
    val res = ds.map(line => {
      val bc_map = bc.value
      val name = line.split(",").filter(StringUtils.isNotBlank(_))(0)

      val gid = bc_map.get(name.hashCode.toLong).get

      gid + "," + line

    })
    res.show(10, false)

    spark.close()
  }
}
