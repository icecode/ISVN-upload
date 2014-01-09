package job

import akka.actor.{Props, ActorSystem}

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-14
 * Time: 下午5:06
 */
object JobActor {
  val uploadActor = ActorSystem("pupload").actorOf(Props[UploadJob], name="uploadjob")
}
