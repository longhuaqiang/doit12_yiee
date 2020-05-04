package cn.doitedu.dw.dict

import java.util.Properties

import ch.hsr.geohash.GeoHash
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by ChenLongWen on 2020/4/20.
  */
object GeohashDict {
  def main(args: Array[String]): Unit = {
    //初始化对象,构造spark
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    // 读取mysql中的gps坐标地理位置表
    val properties = new Properties()
    properties.setProperty("user", "root")
    properties.setProperty("password", "123")

    val df: DataFrame = spark.read.jdbc("jdbc:mysql://localhost:3306/dicts", "area_dict", properties)

    val res: DataFrame = df.map(row => {
      // 取出这一行的经、纬度,省市区信息
      val lat: Double = row.getAs[Double]("lat")
      val lng: Double = row.getAs[Double]("lng")
      val province: String = row.getAs[String]("province")
      val city: String = row.getAs[String]("city")
      val district: String = row.getAs[String]("district")
      val country: String = row.getAs[String]("country")

      // 调用geohash算法，得出geohash编码
      val geoCode: String = GeoHash.geoHashStringWithCharacterPrecision(lat, lng, 6)

      // 组装返回结果
      (geoCode, province, city, district, country)

    }).toDF("geo", "province", "city", "district", "country")

    // 保存结果
//     res.show(10,false)
    res.write.parquet("E:/data/dict/geo_dict/output")
    spark.close()

  }

}
