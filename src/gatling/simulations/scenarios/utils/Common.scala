package utils

import scala.util.Random
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.{ZonedDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.UUID.randomUUID
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.jsonpath.JsonPathCheckType
import com.fasterxml.jackson.databind.JsonNode

object Common {

	val rnd = new Random()
	val now = LocalDate.now()
	val patternDay = DateTimeFormatter.ofPattern("dd")
	val patternMonth = DateTimeFormatter.ofPattern("MM")
	val patternYear = DateTimeFormatter.ofPattern("yyyy")
	val patternDate = DateTimeFormatter.ofPattern("yyyyMMdd")

	def randomString(length: Int) = {
		rnd.alphanumeric.filter(_.isLetter).take(length).mkString
	}

	def getDate(): String = {
		now.format(patternDate)
	}

	def getDay(): String = {
		(1 + rnd.nextInt(28)).toString.format(patternDay).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
	}

	def getMonth(): String = {
		(1 + rnd.nextInt(12)).toString.format(patternMonth).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
	}

	//Dob >= 25 years
	def getMarriageYear(): String = {
		now.minusYears(25 + rnd.nextInt(70)).format(patternYear)
	}

	def getPostcode(): String = {
		randomString(2).toUpperCase() + rnd.nextInt(10).toString + " " + rnd.nextInt(10).toString + randomString(2).toUpperCase()
	}

	// returns the current date or time in format specified in the input pattern e.g. 'yyyy-MM-dd' or 'HH:mm:ss'
	/*def currentDateTime(pattern:String): String = {
		val currentDateTimeFormatted = LocalDateTime.now.format(DateTimeFormatter.ofPattern(pattern))
		currentDateTimeFormatted
	}*/

	// returns the date 1 year from now
	def currentDatePlus1Year(pattern: String): String = {
		now.plusYears(1).format(patternDate)
	}

	// returns the date minus 1 day
  def currentDateMinus1Day(): String = {
		now.minusDays(1).format(patternDate)
	}

  def currentDate(): String = {
    now.format(patternDate)
  }





//	//returns an integer value between a min and max value
//	def getRandomNumberIntBetweenValues(minNumber: Int, maxNumber: Int): Int = {
//		val rand = new scala.util.Random
//		val randNumber = rand.between(minNumber, maxNumber)
//		//println("the number is " + randNumber)
//		randNumber
//	}
//
//	//returns an double value between a min and max value
//	def getRandomNumberDoubleBetweenValues(minNumber:Int, maxNumber:Int): Double = {
//		val rand = new scala.util.Random
//		val randNumber = rand.between(minNumber,maxNumber).asInstanceOf[Double]
//		//println("the number is " + randNumber)
//		randNumber
//	}

	def getUUID(): String = {
		val UUID = randomUUID().toString
		UUID
	}

	//returns an int rounded up to nearest 10
	def roundUpTen(number:Int): Int = {
		if (number % 10 == 0) {
			val roundedNumber = number
			roundedNumber
		}
		else {
			val roundedNumber = (10 - number % 10) + number
			roundedNumber
		}
	}

	//returns an int rounded down to nearest 100
	def roundHundred (number:Int): Int = {
		var roundedNumber = 0
		if (number < 100) {
			roundedNumber = 100
		}
		else {
			roundedNumber = ((number + 50) / 100) * 100;
		}
		roundedNumber
	}
}