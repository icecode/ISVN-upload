import job.UploadJob
import play.api._
import org.squeryl.adapters._
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB

import akka.actor.{ActorSystem, Actor}
import akka.actor.Props

object Global extends GlobalSettings{


  
  override def onStart(app: Application){
    
    implicit val _app = app
    
    SessionFactory.concreteFactory = Some(() =>   
            Session.create(DB.getConnection(), new MySQLAdapter())  
        )
  }
}