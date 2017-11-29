package ioFunctions;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import constants.ErrorID;
import information.Schedule;
import managers.Agenda;

//Thomas Varano
//[Program Descripion]
//Oct 19, 2017

public class SchedWriter
{
   private ObjectOutputStream outStream;
   private FileOutputStream fileStream;
   private boolean debug;
   
   public SchedWriter() {
      init();
   }
   
   private void init() {
      fileStream = null;
      try {
         fileStream = new FileOutputStream(Agenda.FileHandler.FILE_ROUTE);
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
         //TODO why not recoverable?
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
         fileStream.close();
         outStream.close();
      } catch (IOException e) {
         ErrorID.showError(e, false);
      }
   }
}