package com.thetdgroup.components;

/**
 * Pagination navigation functions.
 * <p />
 * These methods are maintain the variables and state when paginating through a list.
 * Given a <code>pageSize</code>, these routines calculate <code>start</code> and <code>end</code>
 * values bounded by 1 and <code>rows</code>.  There is no concept of a current row, only a
 * current page.
 * 
 * Methods are included for testing how many of the 13 controls need to be displayed based on
 * current <code>start</code> and <code>end</code> values.  The controls are:<br /><br />
 * - beginning<br />
 * - much previous ('much' is defined to be 10 times <code>pageSize</code>)<br />
 * - previous<br />
 * - # of page, 3 pages back<br />
 * - # of page, 2 pages back<br />
 * - # of page, 1 page back<br />
 * - # of current page, always present but not a control just displayed<br />
 * - # of page, 1 page next<br />
 * - # of page, 2 pages next<br />
 * - # of page, 3 pages next<br />
 * - next<br />
 * - much next<br />
 * - end<br /><br />
 * which are some of |<, <<, <, -3, -2, -1, 0, 1, 2, 3, >, >>, >|
 * 
 * <p />
 * Sample code may look like:
 <code><br /><br />
 private List data;<br />
 private Paginator paginator;<br /><br />
 public ImplementingClass(...) {<br />
&nbsp;&nbsp;&nbsp;data = new ArrayList();<br />
&nbsp;&nbsp;&nbsp;paginator = new Paginator();<br />
&nbsp;&nbsp;&nbsp;...<br />
&nbsp;&nbsp;&nbsp;paginator.setPageSize(25);  //25 rows to a page<br />
 }<br /><br />
 public List getData() {<br />
&nbsp;&nbsp;&nbsp;return data.subList(paginator.getStart() - 1, paginator.getEnd());<br />
 }<br /><br />
 public void setData(final List data) {<br />
&nbsp;&nbsp;&nbsp;this.data = data;<br />
&nbsp;&nbsp;&nbsp;paginator.setRows(data.size());<br />
 }<br /><br />
 </code>
 * 
 * @author gborek
 * @since 1.0.0
 */

public class Paginator {
   private static final int PAGES_IN_FAST_STEP = 10;

   private int pageSize;
   private int pageSizeTimes2;
   private int pageSizeTimes3;
   private int pageSizeTimesFastStep;
   private int start;
   private int end;
   private int rows;

   public Paginator() {
   }

   public boolean isScrollerControlRequired() { return (rows > pageSize); }
   public boolean isRenderToEnd() { return (start != end); }

   public int getPageSize() { return pageSize; }
   public int getStart()    { return start; }
   public int getEnd()      { return end; }
   public int getRows()     { return rows; }

   public int getScrollerControlNumColumns() {
      //13 Max:  |< << < -3 -2 -1 0 1 2 3 > >> >|
      return 1 + //0 (always present)
             ((start > pageSizeTimesFastStep)
                ? 6  // |< << < -3 -2 -1
                : ((start > pageSizeTimes3)
                     ? 5 // |< < -3 -2 -1
                     : ((start > pageSizeTimes2)
                          ? 4 // |< < -2 -1
                          : ((start > pageSize)
                               ? 3 // |< < -1
                               : 0)))) +
             (((end - pageSize + pageSizeTimesFastStep) < rows)
                ? 6  // 1 2 3 > >> >|
                : (((end + pageSizeTimes2) < rows)
                     ? 5 // 1 2 3 > >|
                     : (((end + pageSize) < rows)
                          ? 4 // 1 2 > >|
                          : ((end < rows)
                               ? 3 // 1 > >|
                               : 0))));
   }
   public boolean isScrollerControlFirst()    { return (start > pageSize); }
   public boolean isScrollerControlMuchPrev() { return (start > pageSizeTimesFastStep); }
   public boolean isScrollerControlPrev()     { return (start > pageSize); }
   public boolean isScrollerControl3Back()    { return (start > pageSizeTimes3); }
   public boolean isScrollerControl2Back()    { return (start > pageSizeTimes2); }
   public boolean isScrollerControl1Back()    { return (start > pageSize); }
   public boolean isScrollerControl1Ahead()   { return (end < rows); }
   public boolean isScrollerControl2Ahead()   { return ((end + pageSize) < rows); }
   public boolean isScrollerControl3Ahead()   { return ((end + pageSizeTimes2) < rows); }
   public boolean isScrollerControlNext()     { return (end < rows); }
   public boolean isScrollerControlMuchNext() { return ((end - pageSize + pageSizeTimesFastStep) < rows); }
   public boolean isScrollerControlLast()     { return (end < rows); }

   public int getScrollerControlCurrentPage() { return 1 + (start / pageSize); }

   public void setScrollerControlCurrentPage(final int page) {
      start = ((page - 1) * pageSize) + 1;
      end = Math.min((start + pageSize) - 1, rows);
   }

   public void scrollerControlFirst() {
      start = 1;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControlMuchPrev() {
      start -= pageSizeTimesFastStep;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControlPrev() {
      start -= pageSize;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControl3Back() {
      start -= pageSizeTimes3;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControl2Back() {
      start -= pageSizeTimes2;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControl2Ahead() {
      start += pageSizeTimes2;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControl3Ahead() {
      start += pageSizeTimes3;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControlNext() {
      start += pageSize;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControlMuchNext() {
      start += pageSizeTimesFastStep;
      end = Math.min((start + pageSize) - 1, rows);
   }
   public void scrollerControlLast() {
      int remainder = rows % pageSize;
      start = Math.max(1, rows - ((remainder == 0) ? pageSize : remainder) + 1);
      end = Math.min((start + pageSize) - 1, rows);
   }

   public void assignPageSize(final int pageSize) {
      this.pageSize = pageSize;
      pageSizeTimes2 = (pageSize * 2);
      pageSizeTimes3 = (pageSize * 3);
      pageSizeTimesFastStep = (pageSize * PAGES_IN_FAST_STEP);
   }

   public void setRows(final int rows) {
      this.rows = rows;
      start = 1;
      end = Math.min(pageSize, rows);
   }

   public void setPageSize(final int pageSize) {
      assignPageSize(pageSize);
      start = 1 + ((start / pageSize) * pageSize);  //note: '/' here truncates
      end = Math.min((start + pageSize) - 1, rows);
   }
}
