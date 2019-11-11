package com.example.mainpostureapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Formatter;
import java.util.Locale;
import java.lang.String;

@Entity(tableName = "data_table")
public class Data
{
    @PrimaryKey
    @NonNull
    private String date;

    private int bottomL;
    private int bottomR;
    private int topL;
    private int topR;
    private int slouch;
    private int totalTime;

    @Ignore
    public Data(int[] errorArray, String date)
    {
        totalTime = errorArray[0];
        bottomL = errorArray[1];
        bottomR = errorArray[2];
        topL = errorArray[3];
        topR = errorArray[4];
        slouch = errorArray[5];
        this.date = date;
    }

    public Data()
    {

    }

    public Data update(Data other)
    {
        int tBottomL = bottomL + other.getBottomL();
        int tBottomR = bottomR + other.getBottomR();
        int tTopL = topL + other.getTopL();
        int tTopR = topR + other.getTopR();
        int tSlouch = slouch + other.getSlouch();
        int tTotalTime = totalTime + other.getSlouch();
        int [] a = {tBottomL, tBottomR, tTopL, tTopR, tSlouch, tTotalTime};
        Data s = new Data(a,date);
        return s;
    }

    public boolean compare(Data other)
    {
        if (this.getDate().equals(other.getDate()))
        {
            return true;
        }
        return false;
    }

    public int getBadTime()
    {
        return  bottomL + bottomR + topL + topR + slouch;
    }
    public int getGoodTime()
    {
        int goodTime = totalTime - getBadTime();
        if (goodTime < 0)
        {
            goodTime = 0;
        }
        return goodTime;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setBottomL(int bottomL) {
        this.bottomL = bottomL;
    }

    public void setBottomR(int bottomR) {
        this.bottomR = bottomR;
    }

    public void setTopL(int topL) {
        this.topL = topL;
    }

    public void setTopR(int topR) {
        this.topR = topR;
    }

    public void setSlouch(int slouch) {
        this.slouch = slouch;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append(date + ",");
        str.append(getStringTotalTime() + ",");
        str.append(getStringGoodTime() + ",");
        str.append(getStringBadTime() + ",");
        str.append(getStringBottomL() + ",");
        str.append(getStringBottomR() + ",");
        str.append(getStringTopL() + ",");
        str.append(getStringTopR() + ",");
        str.append(getStringSlouch());

        return str.toString();

    }

    public int getBottomL()
    {
        return this.bottomL;
    }
    public int getBottomR()
    {
        return this.bottomR;
    }
    public int getTopL()
    {
        return this.topL;
    }
    public int getTopR()
    {
        return this.topR;
    }
    public int getSlouch()
    {
        return this.slouch;
    }
    public int getTotalTime() { return this.totalTime; }
    public String getDate()
    {
        return this.date;
    }

    public String getStringBottomL()
    {
        int seconds = bottomL % 60;
        int minutes = bottomL / 60;
        if (bottomL >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }

    public String getStringBottomR()
    {
        int seconds = bottomR % 60;
        int minutes = bottomR / 60;
        if (bottomR >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }

    public String getStringTopL()
    {
        int seconds = topL % 60;
        int minutes = topL / 60;
        if (topL >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }

    public String getStringTopR()
    {
        int seconds = topR % 60;
        int minutes = topR / 60;
        if (topR >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }

    public String getStringSlouch()
    {
        int seconds = slouch % 60;
        int minutes = slouch / 60;
        if (slouch >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }

    public String getStringTotalTime()
    {
        int seconds = totalTime % 60;
        int minutes = totalTime / 60;
        if (totalTime >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }

    public String getStringGoodTime()
    {
        int time = getGoodTime();
        int seconds = time % 60;
        int minutes = time / 60;
        if (time >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }
    public String getStringBadTime()
    {
        int time = getBadTime();
        int seconds = time % 60;
        int minutes = time / 60;
        if (time >= 3600)
        {
            int minutesToHours = minutes % 60;
            int hours = minutes / 60;
            return String.format(Locale.US,"%02d:%02d:%02d", hours, minutesToHours, seconds);
        }
        else
        {
            return String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
    }
}

