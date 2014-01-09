package helper

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-8
 * Time: 下午5:02
 */
class TimeHelper
object TimeHelper {

  private val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:ii:ss")

  def currentSecond = System.currentTimeMillis()/1000

  def currentSecondMillis = System.currentTimeMillis()

  def format(second:Long):String = format(new java.util.Date(second * 1000))

  def format(date: java.util.Date) = dateFormat.format(date)
}
