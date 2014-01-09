package models

import play.api.libs.iteratee.{Concurrent, Enumerator}

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-18
 * Time: 上午9:58
 */
object OnlineUser {

  private[this] val onlineColl = new java.util.concurrent.ConcurrentHashMap[String, (Enumerator[String], Concurrent.Channel[String])]()

  def get(user:String) = {
    if (onlineColl.containsKey(user)){
      onlineColl.get(user)._1
    }else{
      val (enumerator, pushChannel) = Concurrent.broadcast[String]
      onlineColl.putIfAbsent(user, (enumerator, pushChannel))
      enumerator
    }
  }

  def remove(user:String){
    onlineColl.remove(user)
  }

  def sendMessage(msg:String){
    import scala.collection.JavaConversions._
    onlineColl.values().foreach( _._2.push(msg))
  }

  def broadcast(msg:String){
    import scala.collection.JavaConversions._
    onlineColl.values().foreach( _._2.push(msg))
  }

  def broadcastToUsers(users:List[String], msg:String) {
    import scala.collection.JavaConversions._
    onlineColl.foreach( c => print(c._1))
    users.foreach( su => {
      if (onlineColl.containsKey(su)){
        onlineColl.get(su)._2.push(msg)
      }
    })
  }
}
