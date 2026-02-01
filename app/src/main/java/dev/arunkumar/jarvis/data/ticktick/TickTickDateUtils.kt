package dev.arunkumar.jarvis.data.ticktick

import android.content.Context
import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun Long.formatDueDate(
  context: Context,
  minResolution: Long = DateUtils.DAY_IN_MILLIS,
  transientResolution: Long = DateUtils.DAY_IN_MILLIS,
): String = DateUtils.getRelativeDateTimeString(
  context,
  this,
  minResolution,
  transientResolution,
  0,
).toString()

fun Long.relativeFormattedDate(): CharSequence =
  DateUtils.getRelativeTimeSpanString(this)

private fun isDate(
  given: Long,
  predicate: (given: LocalDate, today: LocalDate) -> Boolean,
): Boolean {
  val instant = Instant.ofEpochMilli(given)
  val zoneId = ZoneId.systemDefault()
  val date = instant.atZone(zoneId).toLocalDate()
  val today = LocalDate.now(zoneId)
  return predicate(date, today)
}

fun Long.isPreviousDays(): Boolean = isDate(this) { d, t -> d.isBefore(t) }
fun Long.isTodayOrAfter(): Boolean = isToday() || isUpcoming()
fun Long.isToday(): Boolean = isDate(this) { d, t -> d.isEqual(t) }
fun Long.isUpcoming(): Boolean = isDate(this) { d, t -> d.isAfter(t) }
