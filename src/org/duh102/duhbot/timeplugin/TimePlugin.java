package org.duh102.duhbot.timeplugin;

import java.util.*;
import java.util.regex.*;

import org.pircbotx.Colors;
import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.MessageEvent;

import org.duh102.duhbot.functions.*;

public class TimePlugin extends ListenerAdapter implements DuhbotFunction
{
  static Pattern commandPattern = Pattern.compile("^\\.(?<command>time|bongs)(?<twelve>12)?(?:[ \\t]+(?<gmtval>GMT[-+][0-9]{1,2}))?");
  static String defaultZone = TimeZone.getDefault().getDisplayName();
  static String zoneStr = ", use GMT+/-(hours) to adjust time zone, default " + defaultZone;
  public ListenerAdapter getAdapter()
  {
    return this;
  }
  public HashMap<String,String> getHelpFunctions()
  {
    HashMap<String,String> helpFunctions = new HashMap<String,String>();
    helpFunctions.put(".time (GMT[+-][0-12])", "Tells you the time (24h)" + zoneStr);
    helpFunctions.put(".time12 (GMT[+-][0-12])", "Tells you the time (12h)" + zoneStr);
    helpFunctions.put(".bongs (GMT[+-][0-12])", "Tells you the big ben time (24h)" + zoneStr);
    helpFunctions.put(".bongs12 (GMT[+-][0-12])", "Tells you the big ben time (12h)" + zoneStr);
    return helpFunctions;
  }
  public String getPluginName()
  {
    return "time plugin";
  }

  public void onMessage(MessageEvent event){
    String msg = Colors.removeFormattingAndColors(event.getMessage());
    Matcher mat = commandPattern.matcher(msg);
    if( mat.matches() ) {
      String twelve = mat.group("twelve");
      String gmt = mat.group("gmtval");
      boolean isTime = mat.group("command").equals("time");
      boolean isTwelve = twelve != null;
      doTime(isTime, isTwelve, gmt, event);
    }
  }
  
  public void doTime(boolean isTime, boolean isTwelve, String gmtVal, Event event) {
    Calendar now = null;
    if( gmtVal != null)
      now = new GregorianCalendar(TimeZone.getTimeZone(gmtVal));
    else
      now = new GregorianCalendar();
    if(isTime) {
      String tFmt = "%1$tT";
      if( isTwelve )
        tFmt = "%1$tr";
      event.respond(String.format(tFmt + " on %1$tF", now));
    } else {
      int hour = now.get(Calendar.HOUR);
      boolean isAM = now.get(Calendar.AM_PM) == Calendar.AM;
      String bongs = null;
      if( hour == 0 )
        bongs = "BONG " + (isAM? "MIDNIGHT": "NOON") + " BONG";
      else {
        if( !isTwelve && !isAM )
          hour+=12;
        StringBuilder bongBuilder = new StringBuilder("BONG");
        if( hour > 1 ) {
          for(int i = 1; i < hour; i++ ){
            bongBuilder.append(" BONG");
          }
        }
        bongs = bongBuilder.toString();
      }
      String amPM = "";
      if( isTwelve )
        amPM = isAM? " AM":" PM" + ",";
      String minSec = String.format("...%2$s and %1$tM:%1$tS on %1$tF", now, amPM);
      event.respond(bongs + minSec);
    }
  }
}
