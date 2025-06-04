package poly.cafe.util;

import java.time.LocalDate;

public class TimeRange {

    private LocalDate begin;
    private LocalDate end;

    public TimeRange() {
        this.begin = LocalDate.now();
        this.end = LocalDate.now();
    }

    public TimeRange(LocalDate begin, LocalDate end) {
        this.begin = begin;
        this.end = end;
    }

    // ✅ THÊM GETTER DƯỚI ĐÂY
    public LocalDate getBegin() {
        return begin;
    }

    public LocalDate getEnd() {
        return end;
    }

    // Các phương thức tạo khoảng thời gian
    public static TimeRange today() {
        LocalDate now = LocalDate.now();
        return new TimeRange(now, now);
    }

    public static TimeRange thisWeek() {
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate end = begin.plusDays(6);
        return new TimeRange(begin, end);
    }

    public static TimeRange thisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate begin = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        return new TimeRange(begin, end);
    }

    public static TimeRange thisQuarter() {
        LocalDate now = LocalDate.now();
        int firstMonth = now.getMonth().firstMonthOfQuarter().getValue();
        LocalDate begin = now.withMonth(firstMonth).withDayOfMonth(1);
        LocalDate end = begin.plusMonths(2).withDayOfMonth(begin.plusMonths(2).lengthOfMonth());
        return new TimeRange(begin, end);
    }

    public static TimeRange thisYear() {
        LocalDate now = LocalDate.now();
        LocalDate begin = now.withMonth(1).withDayOfMonth(1);
        LocalDate end = now.withMonth(12).withDayOfMonth(31);
        return new TimeRange(begin, end);
    }
}
