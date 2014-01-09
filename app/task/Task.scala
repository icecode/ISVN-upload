package task

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-7-9
 * Time: 上午10:08
 */
trait Task extends Runnable {
  def start() {
    new Thread(this).start()
  }
}
