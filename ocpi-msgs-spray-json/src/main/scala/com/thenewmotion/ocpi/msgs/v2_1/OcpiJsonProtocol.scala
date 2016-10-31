package com.thenewmotion.ocpi.msgs.v2_1

import com.thenewmotion.money._
import com.thenewmotion.ocpi.msgs.SimpleStringEnumSerializer
import com.thenewmotion.ocpi.msgs.v2_1.CommonTypes.{BusinessDetails, _}
import com.thenewmotion.ocpi.msgs.v2_1.Credentials.{Creds, CredsResp}
import com.thenewmotion.ocpi.msgs.v2_1.Locations._
import com.thenewmotion.ocpi.msgs.v2_1.Versions._
import com.thenewmotion.time.Imports._
import org.joda.time.format.ISODateTimeFormat
import spray.json._

trait OcpiJsonProtocol extends DefaultJsonProtocol {

  import reflect._

  private val PASS1 = """([A-Z]+)([A-Z][a-z])""".r
  private val PASS2 = """([a-z\d])([A-Z])""".r
  private val REPLACEMENT = "$1_$2"

  // Convert camelCase to snake_case
  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    import java.util.Locale

    def snakify(name: String) = PASS2.replaceAllIn(PASS1.replaceAllIn(name, REPLACEMENT), REPLACEMENT).toLowerCase(Locale.US)

    super.extractFieldNames(classTag).map { snakify }
  }

  implicit val dateTimeOptionalMillisFormat = new JsonFormat[DateTime] {
    val formatterNoMillis = ISODateTimeFormat.dateTimeNoMillis.withZoneUTC
    val formatter = ISODateTimeFormat.dateTime.withZoneUTC
    def write (x: DateTime) = JsString (formatterNoMillis.print (x) )
    def read (value: JsValue) = value match {
      case JsString (x) => formatterNoMillis.parseOption (x) match {
        case Some(parsed) => parsed
        case None => formatter.parseDateTime(x)
      }
      case x => deserializationError ("Expected DateTime as JsString, but got " + x)
    }
  }

  implicit val currencyUnitFormat = new JsonFormat[CurrencyUnit] {
    def write(x: CurrencyUnit) = JsString(x.getCode)
    def read(value: JsValue) = value match {
      case JsString(x) => CurrencyUnit(x)
      case x => deserializationError("Expected CurrencyUnit as JsString, but got " + x)
    }
  }


  implicit val moneyFormat = new JsonFormat[Money] {
    def write(x: Money) = JsObject(
      "currency" -> JsString(x.getCurrencyUnit.toString),
      "amount" -> JsString(x.getAmount.toString)
    )

    def read(value: JsValue) = {
      def err(x: Any) = deserializationError( """Expected Money as ex.: {"currency": "String", "amount": "String"}, but got """ + x)
      value match {
        case JsObject(l) => l.toList match {
          case List(("currency", JsString(cur)), ("amount", JsString(am))) =>
            MoneyFromBigDecimal(BigDecimal(am).bigDecimal).of(CurrencyUnit(cur))
          case x => err(x)
        }
        case x => err(x)
      }
    }
  }

  implicit val capabilityFormat =
    new SimpleStringEnumSerializer[Capability](Capability).enumFormat

  implicit val connectorStatusFormat =
    new SimpleStringEnumSerializer[ConnectorStatus](ConnectorStatus).enumFormat

  implicit val connectorTypeFormat =
    new SimpleStringEnumSerializer[ConnectorType](ConnectorType).enumFormat

  implicit val connectorFormatFormat =
    new SimpleStringEnumSerializer[ConnectorFormat](ConnectorFormat).enumFormat

  implicit val currentTypeFormat =
    new SimpleStringEnumSerializer[PowerType](PowerType).enumFormat

  implicit val pricingUnitFormat =
    new SimpleStringEnumSerializer[PricingUnit](PricingUnit).enumFormat

  implicit val periodTypeFormat =
    new SimpleStringEnumSerializer[PeriodType](PeriodType).enumFormat

  implicit val locationTypeFormat =
    new SimpleStringEnumSerializer[LocationType](LocationType).enumFormat

  implicit val parkingRestrictionTypeFormat =
    new SimpleStringEnumSerializer[ParkingRestriction](ParkingRestriction).enumFormat

  implicit val imageCategoryTypeFormat =
    new SimpleStringEnumSerializer[ImageCategory](ImageCategory).enumFormat

  implicit val facilityTypeFormat =
    new SimpleStringEnumSerializer[Facility](Facility).enumFormat

  implicit val energySourceCategoryTypeFormat =
    new SimpleStringEnumSerializer[EnergySourceCategory](EnergySourceCategory).enumFormat

  implicit val environmentalImpactCategoryTypeFormat =
    new SimpleStringEnumSerializer[EnvironmentalImpactCategory](EnvironmentalImpactCategory).enumFormat

  implicit val energySourceFormat = jsonFormat2(EnergySource)
  implicit val environmentalImpactFormat = jsonFormat2(EnvironmentalImpact)
  implicit val energyMixFormat = jsonFormat5(EnergyMix)
  implicit val powerFormat = jsonFormat3(Power)
  implicit val displayTestFormat = jsonFormat2(DisplayText)
  implicit val geoLocationFormat = jsonFormat2(GeoLocation)
  implicit val additionalGeoLocationFormat = jsonFormat3(AdditionalGeoLocation)
  implicit val regularHoursFormat = jsonFormat3(RegularHours)
  implicit val exceptionalPeriodFormat = jsonFormat2(ExceptionalPeriod)
  implicit val hoursFormat = jsonFormat4(Hours)
  implicit val imageFormat = jsonFormat6(Image)
  implicit val businessDetailsFormat = jsonFormat3(BusinessDetails)

  implicit val connectorFormat = jsonFormat9(Connector)
  implicit val connectorPatchFormat = jsonFormat8(ConnectorPatch)
  implicit val statusScheduleFormat = jsonFormat3(StatusSchedule)
  implicit val evseFormat = jsonFormat13(Evse)
  implicit val evsePatchFormat = jsonFormat12(EvsePatch)
  implicit val operatorFormat = jsonFormat3(Operator)
  implicit val locationFormat = jsonFormat21(Location)


  implicit val locationPatchFormat = jsonFormat20(LocationPatch)
  implicit val locationsRespFormat = jsonFormat4(LocationsResp)
  implicit val locationRespFormat = jsonFormat4(LocationResp)
  implicit val evseRespFormat = jsonFormat4(EvseResp)
  implicit val connectorRespFormat = jsonFormat4(ConnectorResp)

  implicit val endpointIdentifierFormat =
    new SimpleStringEnumSerializer[EndpointIdentifier](EndpointIdentifier).enumFormat
  implicit val versionNumberFormat =
    new SimpleStringEnumSerializer[VersionNumber](VersionNumber).enumFormat

  implicit val versionFormat = jsonFormat2(Version)
  implicit val versionsRespFormat = jsonFormat4(VersionsResp)
  implicit val versionsReqFormat = jsonFormat5(VersionsRequest)
  implicit val endpointFormat = jsonFormat2(Endpoint)
  implicit val versionDetailsFormat = jsonFormat2(VersionDetails)
  implicit val versionDetailsRespFormat = jsonFormat4(VersionDetailsResp)
  implicit val errorRespFormat = jsonFormat3(ErrorResp)
  implicit val successRespFormat = jsonFormat3(SuccessResp)
  implicit val credentialsFormat = jsonFormat5(Creds)
  implicit val credentialsRespFormat = jsonFormat4(CredsResp)
}

object OcpiJsonProtocol extends OcpiJsonProtocol