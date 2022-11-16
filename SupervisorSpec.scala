
import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.event.Logging

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.time.LocalDateTime
import scala.util.Try
object SupervisorSpec extends App {

  case class CreateChild(value: String)

  //case class tellchild(message: String)

  class Supervisor extends Actor with ActorLogging {

    override val supervisorStrategy =
      OneForOneStrategy() {
        case _: NullPointerException => Resume
        case _: Exception => Resume
      }




    override def receive: Receive = {

      case CreateChild(name: String) => {

        val ChildRef = context.actorOf(Props[actorWithInfo])

        ChildRef ! Error("I am a warning log")
      }
    }

  }

  case class Info(message: String)
  case class Warn(message: String)
  case class Close(message: String)
  case class Error(message: String)
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
        Try(new File("src/main/log/logfile.log").renameTo(new File(s"src/main/log/logfile.log_${LocalDateTime.now()}.log"))).getOrElse(false)
        val f = new File("src/main/log/logfile.log")
        if (!f.exists()) {
          f.createNewFile()
        }

      case Error(message) => throw new RuntimeException(sender().toString + "error occured in this actor : message " + message)
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




  sampleActor ! Close("closing the file")

  val systemNew = ActorSystem("Demo")
  val parent = systemNew.actorOf(Props[Supervisor])
  parent ! CreateChild("kid")


  def writeInfo(str: String) {

    writerInfo.write(str)
    writerInfo.newLine()
  }

}