package models

import org.squeryl.annotations.Column
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-8
 * Time: 上午10:26
 */
class UploadFile(val id:Long

                  ,@Column("demand_id")
                  val demandId:Long
                  ,@Column("project_id")
                  val projectId:Long
                  ,@Column("upload_file")
                  val uploadFile:String
                  ,@Column("upload_version")
                  val uploadVersion:Long
                  ,@Column("upload_status")
                  val uploadStatus:Int) extends KeyedEntity[Long]
