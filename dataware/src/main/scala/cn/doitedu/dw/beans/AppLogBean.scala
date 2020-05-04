package cn.doitedu.dw.beans

/**
  * Created by ChenLongWen on 2020/4/30.
  *
  * 封装app埋点日志的额case class
  */
case class AppLogBean(
                       var guid: Long,
                       eventid: String,
                       event: Map[String, String],
                       uid: String,
                       imei: String,
                       mac: String,
                       imsi: String,
                       osName: String,
                       osVer: String,
                       androidId: String,
                       resolution: String,
                       deviceType: String,
                       deviceId: String,
                       uuid: String,
                       appid: String,
                       appVer: String,
                       release_ch: String,
                       promotion_ch: String,
                       longtitude: Double,
                       latitude: Double,
                       carrier: String,
                       netType: String,
                       cid_sn: String,
                       ip: String,
                       sessionId: String,
                       timestamp: Long,
                       var province: String = "未知",
                       var city: String = "未知",
                       var district: String = "未知",
                       var country: String = "未知"


                     )
