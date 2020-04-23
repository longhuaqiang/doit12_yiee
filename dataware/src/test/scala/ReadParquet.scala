import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by ChenLongWen on 2020/4/21.
  */
object ReadParquet {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    import spark.implicits._

    val df: DataFrame = spark.read.parquet("E:\\data\\dict\\geo_dict\\output")

    df.show(10,false)

    spark.close()
  }

}
