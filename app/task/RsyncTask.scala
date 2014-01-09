package task

import scala.sys.process._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-7-9
 * Time: 上午10:09
 */
class RsyncTask(sourceDir:String, val targetAddress:String, val port:String, handler:TaskCallHandler) extends Task {

  def run() {
    try{
      val outString = new StringBuilder()
      val errorString = new StringBuilder()
      Seq("rsync", "-av", "--port=" + port, "--exclude=.svn", sourceDir, targetAddress) ! ProcessLogger( out => outString.append("<br />" + out)
        , err => errorString.append("<br />" + err))
      if(errorString.toString().contains("error:")) {
        handler.onError(new Exception("[rsync]" + targetAddress + errorString.toString()))
      } else {
        handler.onComplete("[rsync]" + targetAddress + outString.toString())
      }
    } catch {
      case e:Throwable => {
        handler.onError(e)
      }
    }
  }

}
