package cn.doitedu.dw.idmap

import cn.doitedu.commons.util.SparkUtils
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by ChenLongWen on 2020/4/27.
  *
  * 3类埋点日志的id映射计算程序
  * (只考虑了当天的数据,没有考虑滚动整合)
  */
object LogDataIdmp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkUtils.getSparkSession(this.getClass.getSimpleName)
    import spark.implicits._

    //加载3类日志
    val appLog: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-11\\app")
    val webLog: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-11\\web")
    val wxAppLog: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-11\\wxapp")

    //提取每一类数据中每一行数据的标识字段
    val appIds: RDD[Array[String]] = extractIds(appLog)
    val webIds: RDD[Array[String]] = extractIds(webLog)
    val wxAppIds: RDD[Array[String]] = extractIds(wxAppLog)
    //将rdd整合
    val ids: RDD[Array[String]] = appIds.union(webIds).union(wxAppIds)

    //构造一个点rdd集合
    val appVertices: RDD[(Long, String)] = vertices(ids)


    //构造一个边rdd
    val appEdges: RDD[Edge[String]] = edges(ids)

    //用点集合 和 边集合构造一张图
    val graph: Graph[String, String] = Graph(appVertices,appEdges)
    //调用图的算法: 连通子图算法
    val graph2: Graph[VertexId, String] = graph.connectedComponents()
    //VertexRDD[VertexId] ==> RDD[(点id-Long,组中的最小值)]
    val vertices2: VertexRDD[VertexId] = graph2.vertices
    //可以直接用图计算所产生的结果中的组最小值,作为这一组中的guid,当然也可以自己另外生成一个UUID来作为GUID

    //将结果转换成易于保存的格式,并且给一个表头
    //保存结果
    vertices2.toDF("biaoshi_hashcode","guid").write.parquet("data/idmp/2020-04-28")
    spark.close()
  }


  //提取每一类数据中每一行数据的标识字段
  def extractIds(logDs:Dataset[String]):RDD[Array[String]]={
    logDs.rdd.map(line => {
      val jsonObj: JSONObject = JSON.parseObject(line)
      //从json对象中取出user对象
      val user: JSONObject = jsonObj.getJSONObject("user")
      val uid: String = user.getString("uid")

      //从user对象中取出phone对象
      val phoneObj: JSONObject = user.getJSONObject("phone")
      val imie: String = phoneObj.getString("imie")
      val mac: String = phoneObj.getString("mac")
      val imsi: String = phoneObj.getString("imsi")
      val androidId: String = phoneObj.getString("androidId")
      val deviceId: String = phoneObj.getString("deviceId")
      val uuid: String = phoneObj.getString("uuid")

      Array(uid, imie, mac, imsi, androidId, deviceId, uuid).filter(StringUtils.isNoneBlank(_))

    })
  }
  //构造一个点rdd集合
  def vertices(ids:RDD[Array[String]]):RDD[(Long, String)]={

    ids.flatMap(fields => {

      for (ele <- fields) yield (ele.hashCode.toLong, ele)

    })

  }
  //构造一个边rdd
  //对当天“边集合”进行聚合计数，过滤掉(频次<阈值)的边，得到“边集合”
  //注意:因为此处是一个数组,而不是一个双元组,所以需要双重循环
  // [a,b,c,d] ==> a-b a-c a-d b-c b-d c-d
  def edges(ids:RDD[Array[String]]):RDD[Edge[String]]={
    ids.flatMap(fields => {
      for (i <- 0 to fields.length - 2;j<-i+1 to fields.length-1) yield Edge(fields(i).hashCode.toLong, fields(j).hashCode.toLong, "")
    })
      .map(edge=>(edge,1))
      .reduceByKey(_+_)
      .filter(_._2 > 2)
//      .map(edge=>edge._1)
      .map(_._1)
  }

}
