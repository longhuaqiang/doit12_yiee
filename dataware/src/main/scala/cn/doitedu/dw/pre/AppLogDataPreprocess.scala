package cn.doitedu.dw.pre

import java.util

import ch.hsr.geohash.GeoHash
import cn.doitedu.commons.util.SparkUtils
import cn.doitedu.dw.beans.AppLogBean
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Dataset, Row, SparkSession}

/**
  * Created by ChenLongWen on 2020/4/29.
  *
  * app埋点日志预处理
  */
object AppLogDataPreprocess {

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkUtils.getSparkSession(this.getClass.getSimpleName)
    import spark.implicits._

    //1.加载当日的app埋点数据
    val ds: Dataset[String] = spark.read.textFile("E:\\DOIT12-DW\\DOIT12-综合项目-DAY03-日志预处理\\yiee_logs\\2020-01-12\\app")

    //加载geo地里位置字典,并收集到driver端,然后广播出去
    val geoMap = spark.read.parquet("data/dict/geo_dict/output")
      .rdd
      .map({
        case Row(geo: String, province: String, city: String, district: String, country: String) =>
          (geo, (province, city, district, country))
      }).collectAsMap()

    val bc_geo: Broadcast[collection.Map[String, (String, String, String, String)]] = spark.sparkContext.broadcast(geoMap)

    //加载idmp映射字典,并收集到driver端,然后广播出去
    val idmp = spark.read.parquet("data/idmp/2020-01-12")
      .rdd
      .map({
        case Row(biaoshi_hashcode: Long, guid: Long) => {
          (biaoshi_hashcode, guid)
        }
      }).collectAsMap()
    val bc_idmp: Broadcast[collection.Map[Long, Long]] = spark.sparkContext.broadcast(idmp)

    //2.规范化结果

    //解析json
    val res = ds.map(line => {
      //可以将json直接转换成JavaBean对象,但是不够灵活
      // val appBeanClass: AppBeanClass = JSON.parseObject[AppBeanClass](line, Class.forName("cn.doit.bean.AppBeanClass"))
      var bean: AppLogBean = null
      try {
        val jsonobj = JSON.parseObject(line)
        val eventid = jsonobj.getString("eventid")
        val timestamp = jsonobj.getString("timestamp").toLong

        val eventobj: JSONObject = jsonobj.getJSONObject("event")
        import scala.collection.JavaConversions._
        val javaMap: Map[String, String] = eventobj.getInnerMap.asInstanceOf[util.Map[String, String]].toMap
        val event: Map[String, String] = javaMap

        val userobj = jsonobj.getJSONObject("user")
        val uid = userobj.getString("uid")
        val sessionId = userobj.getString("sessionId")

        val phoneobj = userobj.getJSONObject("phone")
        val imei = phoneobj.getString("imei")
        val mac = phoneobj.getString("mac")
        val imsi = phoneobj.getString("imsi")
        val osName = phoneobj.getString("osName")
        val osVer = phoneobj.getString("osVer")
        val androidId = phoneobj.getString("androidId")
        val resolution = phoneobj.getString("resolution")
        val deviceType = phoneobj.getString("deviceType")
        val deviceId = phoneobj.getString("deviceId")
        val uuid = phoneobj.getString("uuid")


        val appobj = userobj.getJSONObject("app")
        val appid = appobj.getString("appid")
        val appVer = appobj.getString("appVer")
        val release_ch = appobj.getString("release_ch") // 下载渠道
        val promotion_ch = appobj.getString("promotion_ch") // 推广渠道

        val locobj = userobj.getJSONObject("loc")

        var lng = 0.0
        var lat = -90.0

        try {
          lng = locobj.getDouble("longtitude")
          lat = locobj.getDouble("latitude")
        } catch {
          case e: Exception => e.printStackTrace()
        }

        val carrier = locobj.getString("carrier")
        val netType = locobj.getString("netType")
        val cid_sn = locobj.getString("cid_sn")
        val ip = locobj.getString("ip")

        // 判断数据合法规则
        val tmp = (imei + imsi + mac + uid + uuid + androidId).replaceAll("null", "")
        if (StringUtils.isNotBlank(tmp) && event != null && StringUtils.isNotBlank(eventid) && StringUtils.isNotBlank(sessionId)) {
          // 将提取出来的各个字段，封装到AppLogBean中
          bean = AppLogBean(
            Long.MinValue,
            eventid,
            event,
            uid,
            imei,
            mac,
            imsi,
            osName,
            osVer,
            androidId,
            resolution,
            deviceType,
            deviceId,
            uuid,
            appid,
            appVer,
            release_ch,
            promotion_ch,
            lng,
            lat,
            carrier,
            netType,
            cid_sn,
            ip,
            sessionId,
            timestamp
          )
        }

      } catch {
        case e: Exception => {
          e.printStackTrace()
        }
      }
      bean
    })
      //如果数据符合要求,就返回一个AppLogBean,如果不符合要求,就返回一个null,在下一步中过滤掉
      .filter(_ != null)
      .map(bean => {
        val geo = bc_geo.value
        val idmp: collection.Map[Long, Long] = bc_idmp.value
        //数据集成 -->省市区
        // 查geo地域字典，填充省市区
        val lat: Double = bean.latitude
        val lng: Double = bean.longtitude
        val mygeo: String = GeoHash.geoHashStringWithCharacterPrecision(lat, lng, 6)
        val maybeTuple: Option[(String, String, String, String)] = geo.get(mygeo)
        if (maybeTuple.isDefined) {
          val areaName: (String, String, String, String) = maybeTuple.get
          bean.province = areaName._1
          bean.city = areaName._2
          bean.district = areaName._3
          bean.country = areaName._4
        }

        //数据集成 -->guid
        // 查id映射字典，填充guid
        val ids = Array(bean.imei, bean.imsi, bean.mac, bean.androidId, bean.uuid, bean.uid)
        var find = false
        for (elem <- ids if !find) {
          val maybeLong: Option[Long] = idmp.get(elem.hashCode.toLong)
          if (maybeLong.isDefined) {
            bean.guid = maybeLong.get
            find = true
          }
        }
        bean
      })
      .filter(bean => bean.guid != Long.MinValue)
      .toDF()
//      .show(50, false)
      .write
      .parquet("data/applog_processed/2020-01-12")

    spark.close()
  }

}
