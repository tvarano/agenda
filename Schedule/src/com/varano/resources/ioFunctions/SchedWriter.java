package com.varano.resources.ioFunctions;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.varano.information.Schedule;
import com.varano.information.constants.ErrorID;

//Thomas Varano
//[Program Descripion]
//Oct 19, 2017

public class SchedWriter
{
   private ObjectOutputStream outStream;
   private FileOutputStream fileStream;
   private boolean debug;
   
   public SchedWriter(String route) {
      init(route);
   }
   
   public SchedWriter() {
   		this(com.varano.managers.FileHandler.SCHED_ROUTE);
   }
   
   private void init(String route) {
      fileStream = null;
      try {
         fileStream = new FileOutputStream(route);
      } catch (FileNotFoundException e) {
         ErrorID.showError(e, false);
      }
      try {
         outStream = new ObjectOutputStream(fileStream);
      } catch (IOException e) {
         ErrorID.showError(e, false);
      }
   }
   
   public void write(Schedule s) {
      if (s == null) {
         if (debug) System.err.println("written schedule is null");
         ErrorID.showError(new NullPointerException(), false);
         return;
      }
      try {
         outStream.writeObject(s);
      } catch (IOException e) {
         ErrorID.showError(e, false);
      }
      close();
   }
   
   public void close() {
      try {
         fileStream.flush();
         fileStream.close();
         outStream.flush();
         outStream.close();
      } catch (IOException e) {
         ErrorID.showError(e, true);
      }
   }
}