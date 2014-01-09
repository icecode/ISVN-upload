package task

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-7-9
 * Time: 上午10:09
 */
trait TaskCallHandler {
  def onComplete(result:String):Unit
  def onError(e:Throwable):Unit
}
