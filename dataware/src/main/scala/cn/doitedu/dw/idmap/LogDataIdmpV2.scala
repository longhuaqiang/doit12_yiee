package cn.doitedu.dw.idmap

import cn.doitedu.commons.util.SparkUtils
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  * 考虑上一日的idmp字典整合的idmapping程序
  */
object LogDataIdmpV2 {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkUtils.getSparkSession(this.getClass.getSimpleName)
    import spark.implicits._

    //一.加载3类日志
    val appLog: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-12\\app")
    val webLog: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-12\\web")
    val wxAppLog: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-12\\wxapp")

    //二.提取每一类数据中每一行数据的标识字段
    val appIds: RDD[Array[String]] = extractIds(appLog)
    val webIds: RDD[Array[String]] = extractIds(webLog)
    val wxAppIds: RDD[Array[String]] = extractIds(wxAppLog)
    //将rdd整合
    val ids: RDD[Array[String]] = appIds.union(webIds).union(wxAppIds)

    //三.构造一个点rdd集合
    val appVertices: RDD[(Long, String)] = vertices(ids)


    //四.构造图计算中的Edge集合
    val appEdges: RDD[Edge[String]] = edges(ids)

    //五.将上一日的idmp映射字典,解析成点,边集合
    val preDayIdmp: DataFrame = spark.read.parquet("data/idmp/2020-01-11")
    //构造点集合
    val preDayIdmpVertices: RDD[(VertexId, String)] = preDayIdmp.rdd.map(row => {
      val idFlag: VertexId = row.getAs[VertexId]("biaoshi_hashcode")
      (idFlag, "")
    })
    //构造边集合
    val preDayEdges: RDD[Edge[String]] = preDayIdmp.rdd.map(row => {
      val idFlag: VertexId = row.getAs[VertexId]("biaoshi_hashcode")
      val guid: VertexId = row.getAs[VertexId]("guid")
      Edge(idFlag, guid, "")
    })


    //六,七.将当日的点集合union上日的点集合,当日的边集合union上日的边集合,构造成图,并调用连通子图算法
    val graph: Graph[String, String] = Graph(appVertices.union(preDayIdmpVertices), appEdges.union(preDayEdges))
    //调用图的算法: 连通子图算法
    val graph2: Graph[VertexId, String] = graph.connectedComponents()
    //VertexRDD[VertexId] ==> RDD[(点id-Long,组中的最小值)]

    val vertices2: VertexRDD[VertexId] = graph2.vertices


    //八.将结果跟上日的映射字典作对比,调整guid
    //0.将上一日的idmp映射结果字典收集到driver端,并广播
    val idMap: collection.Map[VertexId, VertexId] = preDayIdmp.rdd.map(row => {
      val idFlag: VertexId = row.getAs[VertexId]("biaoshi_hashcode")
      val guid: VertexId = row.getAs[VertexId]("guid")
      (idFlag, guid)
    }).collectAsMap()
    val bc: Broadcast[collection.Map[VertexId, VertexId]] = spark.sparkContext.broadcast(idMap)
    //1.将今日的图计算结果按guid进行分组,得到 (guid,(id1,id2...idx))
    //2.然后去跟昨日的映射字典进行对比
    //3.如果取到值,就将今日的guid替换成昨日的guid
    val todayIdmpResult: RDD[(VertexId, Iterable[VertexId])] = vertices2.map(tp => (tp._2, tp._1))
      .groupByKey()
      .mapPartitions(iter => {
        val preIdmap: collection.Map[VertexId, VertexId] = bc.value
        iter.map(tp => {
          //当日的guid计算结果
          var todayGuid: VertexId = tp._1
          //这一组中所有的id标识
          val ids = tp._2

          var find = false
          for (elem <- ids if !find) {
            val maybeGuid: Option[VertexId] = preIdmap.get(elem)
            //如果这个id在昨天的映射字典中找到了,那么就用昨天的guid替换掉今天这一组中的guid
            if (maybeGuid.isDefined) {
              todayGuid = maybeGuid.get
              find = true
            }
          }
          (todayGuid, ids)
        })

      })
    val result: RDD[(VertexId, VertexId)] = todayIdmpResult.flatMap(tp => {
      val guid: VertexId = tp._1
      val ids: Iterable[VertexId] = tp._2
      for (elem <- ids) yield (elem, guid)
    })



    //可以直接用图计算所产生的结果中的组最小值,作为这一组中的guid,当然也可以自己另外生成一个UUID来作为GUID

    //将结果转换成易于保存的格式,并且给一个表头
    //保存结果
    result.toDF("biaoshi_hashcode", "guid").write.parquet("data/idmp/2020-01-12")
    spark.close()
  }


  //提取每一类数据中每一行数据的标识字段
  def extractIds(logDs: Dataset[String]): RDD[Array[String]] = {
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
  def vertices(ids: RDD[Array[String]]): RDD[(Long, String)] = {

    ids.flatMap(fields => {

      for (biaoshi <- fields) yield (biaoshi.hashCode.toLong, biaoshi)

    })

  }

  //构造一个边rdd
  //对当天“边集合”进行聚合计数，过滤掉(频次<阈值)的边，得到“边集合”
  //注意:因为此处是一个数组,而不是一个双元组,所以需要双重循环,对一个数组中的所有标识进行两两组合成边
  // [a,b,c,d] ==> a-b a-c a-d b-c b-d c-d
  def edges(ids: RDD[Array[String]]): RDD[Edge[String]] = {
    ids.flatMap(fields => {
      for (i <- 0 to fields.length - 2; j <- i + 1 to fields.length - 1) yield Edge(fields(i).hashCode.toLong, fields(j).hashCode.toLong, "")
    })
      //将边变成(边,1)来计算一个边出现的次数
      .map(edge => (edge, 1))
      .reduceByKey(_ + _)
      //过滤掉出现次数小于经验阈值的边
      .filter(_._2 > 2)
      //      .map(edge=>edge._1)
      .map(_._1)
  }

}
