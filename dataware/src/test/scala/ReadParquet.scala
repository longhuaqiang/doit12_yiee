import cn.doitedu.commons.util.SparkUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by ChenLongWen on 2020/4/21.
  */
object ReadParquet {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    val spark: SparkSession = SparkUtils.getSparkSession(this.getClass.getSimpleName)
    import spark.implicits._

    val df: DataFrame = spark.read.parquet("data/applog_processed/2020-01-12")

    df.show(10,false)

    spark.close()
  }

}
