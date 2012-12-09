package de.guderlei.spray.api

import spray.json.{JsValue, JsString, JsonFormat, DefaultJsonProtocol}
import de.guderlei.spray.domain.TodoItem
import java.util.{GregorianCalendar, Date}
import javax.xml.bind.DatatypeConverter
import spray.httpx.SprayJsonSupport

/**
 * Customized JSON Protocol
 */
trait MyJsonMarshaller extends DefaultJsonProtocol with SprayJsonSupport{

  /**
   * Converter for Dates to ISO format
   */
  implicit object DateFormat extends JsonFormat[Date] {
    def write(obj: Date) = {
      val cal = new GregorianCalendar()
      cal.setTime(obj)
      JsString(DatatypeConverter.printDateTime(cal))
    }

    def read(json: JsValue): Date = json match {
      case JsString(s) => DatatypeConverter.parseDateTime(s).getTime
    }
  }

  implicit val itemFormat = jsonFormat3(TodoItem)
}
