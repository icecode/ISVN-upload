package job

import akka.actor.Actor
import models._
/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-14
 * Time: 下午4:49
 */

case class UploadDemand(demand:Demand)
case class RollbackDemand(demand:Demand)

class UploadJob extends Actor {

  def receive = {

    case UploadDemand(demand) => {
      demand.uploadToServer()
    }

    case RollbackDemand(demand) => {
      demand.rollbackToServer()
    }

  }

}
