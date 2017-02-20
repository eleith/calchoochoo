package com.eleith.calchoochoo.utils;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class InfinitePagerDataDates extends InfinitePagerData<LocalDate> {
  private LocalDate today = new LocalDate();
  private LocalDate tomorrow = today.plusDays(1);
  private LocalDate yesterday = today.minusDays(1);
  private DateTimeFormatter dateDisplayFormat = DateTimeFormat.forPattern("MMM dd, yyyy");

  public InfinitePagerDataDates(LocalDate[] dataArray) {
    super(dataArray);
  }

  public InfinitePagerDataDates(LocalDate initialDate) {
    super(initialDate);
  }

  @Override
  public LocalDate getNextData() {
    return getData(getDataSize() - 1).plusDays(1);
  }

  @Override
  public LocalDate getPreviousData() {
    return getData(0).minusDays(1);
  }

  @Override
  public String getTextFor(int position) {
    if (getData(position).isEqual(today)) {
      return "today";
    } else if (getData(position).isEqual(tomorrow)) {
      return "tomorrow";
    } else if (getData(position).isEqual(yesterday)) {
      return "yesterday";
    } else {
      return dateDisplayFormat.print(getData(position));
    }
  }
}
