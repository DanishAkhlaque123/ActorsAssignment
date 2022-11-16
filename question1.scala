
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.time.LocalDateTime
import scala.util.Try
object question1 extends App {



  case class Info(message: String)
  case class Warn(message: String)
  case class Close(message: String)
  class actorWithInfo extends Actor with ActorLogging {

    val logger = Logging(context.system, this)

    override def receive: Receive = {

      case Info(message) =>
        log.info(message)
        writeInfo(message)


      case Warn(message) => log.warning(message)
        writeInfo(message)

      case Close(message) =>
        writerInfo.close
        Try(new File("src/main/log/logfile.log").renameTo(new File(s"src/main/log/logfile.log_${ LocalDateTime.now()}.log"))).getOrElse(false)
        val f = new File("src/main/log/logfile.log")
        if (!f.exists()) {
          f.createNewFile()
        }
    }


  }

  val writerInfo: BufferedWriter = {
    val f = new File("src/main/log/logfile.log")
    if (!f.exists()) {
      f.createNewFile()
    }
    new BufferedWriter(new FileWriter(f, true))
  }


  val system = ActorSystem("LoggingQuestion1")
  val sampleActor = system.actorOf(Props(new actorWithInfo))


  sampleActor ! Info("Hi my name is Danish")

  sampleActor ! Warn("Hi my name is Tabish")

  sampleActor ! Warn("Hi my name is Tabish1")
  sampleActor ! Warn("Hi my name is Tabish2")


  sampleActor ! Close("closing the file")




  def writeInfo(str: String) {

    writerInfo.write(str)
    writerInfo.newLine()
  }


}