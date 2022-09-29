/*BEGINPROLOGUE****************************************************************
 * @copyright(disclaimer)                                                     *
 *                                                                            *
 * DISCLAIMER OF WARRANTIES.                                                  *
 *                                                                            *
 * The following IBM Content Manager Enterprise Edition code is sample code   *
 * created by IBM Corporation. IBM grants you a nonexclusive copyright        *
 * license to use this sample code example to generate similar function       *
 * tailored to your own specific needs. This sample code is not part of any   *
 * standard IBM product and is provided to you solely for the purpose of      *
 * assisting you in the development of your applications. This example has    *
 * not been thoroughly tested under all conditions. IBM, therefore cannot     *
 * guarantee nor may you imply reliability, serviceability, or function of    *
 * these programs. The code is provided "AS IS", without warranty of any      *
 * kind. IBM shall not be liable for any damages arising out of your or any   *
 * other parties use of the sample code, even if IBM has been advised of the  *
 * possibility of such damages. If you do not agree with these terms, do not  *
 * use the sample code.                                                       *
 *                                                                            *
 * Licensed Materials - Property of IBM                                       *
 * 5724-B19, 5697-H60                                                         *
 * © Copyright IBM Corp. 1994, 2013 All Rights Reserved.                      *
 *                                                                            *
 * US Government Users Restricted Rights - Use, duplication or disclosure     *
 * restricted by GSA ADP Schedule Contract with IBM Corp.                     *
 *                                                                            *
 * @endCopyright                                                              *
 ******************************************************************************
 
 Attempt Manager
     For internal use by TExportManagerICM and TImportManagerICM only.
     
     This object is responsible for attempt counting and configuration
     relating to auto-recovery and retry features of the Import / Export
     Management Tools.
     
     For more information on the Import / Export Managmenet Tools, refer to
     the header doucmentation in the TExportManagerICM.java sample file.
 
 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: TExportManagerICM_AttemptManager.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Object responsible for attempt counting and configuration.  This is
 *                    used by TExportManagerICM and TImportManagerICM.
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: none.  Intenral use by TExportManagerICM & TImportManagerICM only.
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: none.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: none.
 ************************************************************************************************/
public class TExportManagerICM_AttemptManager{

    // Variables
    //  - Configuration Variables
    int     _delayAtAttemptNum    = 0;      // Attempt after which delays are enforced.
    long    _delayTimeMS          = 0;
    String  _iniFileName          = null;
    int     _maxAttempts          = 0;
    //  - Internal Variables
    int     _attemptNum           = 0;      // Actual attempts start at '1'.  '0' means prior to first attempt.
    boolean _complete             = false;  // Whether or not the operation that it is managing completed successfully.
    String  _name                 = null;
    boolean _terminated           = false;  // Whether or not a fatal error has happened to stop any further attempts.
    //  - Object Veriables
    TExportPackageICM.Options        _options              = null;
    TExportManagerICM_AttemptManager _parentAttemptManager = null;
    
   /**
    * Preventing creation with no arguments.
    **/
    private TExportManagerICM_AttemptManager() throws Exception{
        throw new Exception("The TExportManagerICM_AttemptManager object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Export Manager.
    * @param name - Name of this attempt manager to be used in errors, warnings, and prompts.
    * @param commandlineArgs - Command line arguments from main(String[] argsv)
    **/
    public TExportManagerICM_AttemptManager(String name, String[] commandlineArgs) throws IllegalArgumentException, FileNotFoundException, Exception{
        initCTOR(null,name,commandlineArgs);
    }//end CTOR(String name, String[] commandlineArgs)    

   /**
    * Create a child instance of the Export Manager.  This attempt manager
    * yields in error conditions to rety attempts of a parent attempt manager.
    * @param parentAttemptManager - Parent Attempt Manager. (required)
    * @param name                 - Name of this attempt manager to be used in errors, warnings, and prompts.
    * @param commandlineArgs      - Command line arguments from main(String[] argsv)
    **/
    public TExportManagerICM_AttemptManager(TExportManagerICM_AttemptManager parentAttemptManager, String name, String[] commandlineArgs) throws IllegalArgumentException, FileNotFoundException, Exception{
        // Validate unique input to this CTOR
        if(parentAttemptManager==null) throw new InternalError("Error creating child Attempt Manager '"+name+"'.  The constructor used indicates a child attempt manager, but the parent attempt manager reference was 'null'.  A valid instance of a parent attempt manager is required.");
        // Call common constructor init.
        initCTOR(parentAttemptManager,name,commandlineArgs);
    }//end CTOR(ParentManager, String name, String[] commandlineArgs)    

   /**
    * Initialize all variables.  This should be called by every valid constructor.
    * @param parentAttemptManager - Parent Attempt Manager. Use 'null' for no parent.
    * @param name                 - Name of this attempt manager to be used in errors, warnings, and prompts.
    * @param commandlineArgs      - Command line arguments from main(String[] argsv)
    **/
    public void initCTOR(TExportManagerICM_AttemptManager parentAttemptManager, String name, String[] commandlineArgs) throws IllegalArgumentException, FileNotFoundException, Exception{
        // Save Parameter Variables
        _parentAttemptManager = parentAttemptManager;
        _name                 = name;
        // Start off with defaults
        initByDefaults();
        // Override Settings by Configuration File
        initByConfigurationFile(commandlineArgs);
        // Initialize Settings by Parse Command Line Arguments
        initByCommandline(commandlineArgs);
        // Create Objects
        initObjects(commandlineArgs);
        // Prompt User for Remaining That Have No Defaults.
        initMissingOptionsByPrompting();
    }//end init(ParentManager, String name, String[] commandlineArgs)    

   /**
    * Start off with the defaults.  These can be later overridden.
    **/
    private void initByDefaults(){
        // Set defaults for optional command line arguments.
        _attemptNum        = 0;     // Actual attempts start at '1'.  '0' means prior to first attempt.
        _complete          = false;
        _delayAtAttemptNum = TExportManagerICM.DEFAULT_RETRY_DELAY_AT_ATTEMPT_NUM + 1; // Attempt after which delays are enforced.
        _delayTimeMS       = TExportManagerICM.DEFAULT_RETRY_DELAY_MS;
        _iniFileName       = TExportManagerICM.DEFAULT_INI_FILE_NAME;
        _maxAttempts       = TExportManagerICM.DEFAULT_RETRY_ATTEMPTS + 1; // Number of retry (Retry attempt 1 comes after attempt 1 and is attempt 2.)
        _options           = null;
        _terminated        = false;
    }//end initMissingOptionsByDefaults()

   /**
    * Initialize the settings based on the environment variables.  It will use
    * the configuration file name specified by command line arguments, but if
    * none specified, will use defaults.
    * @param commandlineArgs - Command line arguments from main(String argv[]).
    **/
    private void initByConfigurationFile(String[] commandlineArgs) throws IllegalArgumentException, FileNotFoundException, IOException{

        // First, check the command line arguments for the file name first.
        String  iniFileName    = TExportManagerICM.getCommandlineChoice(commandlineArgs,"-i","-ini"     ,false,"-i/ini <Alternate Configuration File>");
        // If specified at command line, save value
        if(iniFileName!=null)
            _iniFileName = iniFileName;
        // Ensure that the ini file specified actually exists.
        TExportManagerICM.validateFileExists(_iniFileName,"If a configuration file is specified, it must exist and appear in the proper directory.");

        // Parse file, reading values.
        // Open File
        FileReader fileReader = new FileReader(_iniFileName);
        BufferedReader file   = new BufferedReader(fileReader);

        // Scan line by line, reading entries applicable to this object.
        String line = null;
        while((line = file.readLine())!=null){ // Continue until reach end of file.
            int separatorLoc = line.indexOf("=");
            if(separatorLoc > 0){
                String property = line.substring(0,separatorLoc).trim();
                String value    = line.substring(separatorLoc+1).trim();
                // Handle depending on property name
                // Delay At Attempt Num
                if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_DELAY_AT_RETRY_NUM)==0){
                    if(value.compareTo("")!=0){  // Only save non-blank values.
                        // Convert String to Numeric
                        try{_delayAtAttemptNum = Integer.valueOf(value).intValue() + 1;} // add one since retries come after the first attempt.
                        catch(Exception exc){ throw new IllegalArgumentException("Invalid property value in configuration file '"+_iniFileName+"'.  Property '"+property+"' value '"+value+"'.  This property requires an integer value."); }
                    }
                }
                // Delay Time in MS
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_RETRY_DELAY_MS)==0){
                    if(value.compareTo("")!=0){  // Only save non-blank values.
                        // Convert String to Numeric
                        try{_delayTimeMS = Long.valueOf(value).longValue();}
                        catch(Exception exc){ throw new IllegalArgumentException("Invalid property value in configuration file '"+_iniFileName+"'.  Property '"+property+"' value '"+value+"'.  This property requires an integer value."); }
                    }
                }
                // Max Retry Attempts
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_RETRY_ATTEMPTS)==0){
                    if(value.compareTo("")!=0){  // Only save non-blank values.
                        // Convert String to Numeric
                        try{_maxAttempts = Integer.valueOf(value).intValue() + 1;} // Add one because the first retry comes after the first attempt.
                        catch(Exception exc){ throw new IllegalArgumentException("Invalid property value in configuration file '"+_iniFileName+"'.  Property '"+property+"' value '"+value+"'.  This property requires an integer value."); }
                    }
                }
            }//end if(separatorLoc > 0){
        }//end while((line = file.readLine())!=null){
            
        // Close File
        file.close();
    }//end validateSettings

   /**
    * For any settings specified at the command line, override the defaults
    * specified.  The command line arguemnts will be validated.
    * @param argv - Command line arguments from main(String args[])
    **/
    private void initByCommandline(String argv[]) throws Exception{
        //------------------------------------------------------------
        // Parse input parameters
        //--------------------------------------------------------------
        // -i/ini <Alternate Configuration File>
        String  iniFileName     = TExportManagerICM.getCommandlineChoice(argv,"-i","-ini"     ,false,"-i/ini <Alternate Configuration File>");
        
        // Save any non-null settings
        if(iniFileName     !=null) _iniFileName     = iniFileName;
        
    }//end initByCommandline()
    
   /**
    * For any settings that are not currently set (such as not specified
    * by the user through the command line and did not have a default, prompt
    * the user.
    **/
    private void initMissingOptionsByPrompting(){
        // Nothing to prompt for.
    }//end initMissingOptionsByDefaults()

   /**
    * Create any object needed, such as attempt managers.
    * @param commandlineArgs - Command line arguments from main.
    **/
    private void initObjects(String[] commandlineArgs) throws FileNotFoundException, Exception{
        // Create Objects
        _options = new TExportPackageICM.Options(_iniFileName);
    }//end initObjects()

   /**
    * When an exception is caught by the restartable operation that this
    * AttemptManager is managing, this method should be called to determine
    * whether a warning should be printed or if the operation should be terminated,
    * throwing an exception.
    **/
    public void handleAttemptFailure(Exception exc) throws Exception{
        System.out.println("");
        System.out.println("--- Attempt Failure Detected:");
        System.out.println("");
        System.out.println("*********************************************");
        if(_terminated){ // If terminated by fatal error...
          System.out.println("*  FFFFFF   AAAA   TTTTTTT   AAAA   L       *");
          System.out.println("*  F       A    A     T     A    A  L       *");
          System.out.println("*  F       A    A     T     A    A  L       *");
          System.out.println("*  FFFF    AAAAAA     T     ATTTTA  L       *");
          System.out.println("*  F       A    A     T     A    A  L       *");
          System.out.println("*  F       A    A     T     A    A  L       *");
          System.out.println("*  F       A    A     T     A    A  LLLLLL  *");
          System.out.println("*                                           *");
        }//end if(_terminate)_ // If terminated by fatal error...
        System.out.println("*  EEEEEE  RRRRR   RRRRR    OOOO   RRRRR    *");
        System.out.println("*  E       R    R  R    R  O    O  R    R   *");
        System.out.println("*  E       R    R  R    R  O    O  R    R   *");
        System.out.println("*  EEEE    RRRRR   RRRRR   O    O  RRRRR    *");
        System.out.println("*  E       R  R    R  R    O    O  R  R     *");
        System.out.println("*  E       R   R   R   R   O    O  R   R    *");
        System.out.println("*  EEEEEE  R    R  R    R   OOOO   R    R   *");
        System.out.println("*********************************************");
        System.out.println("   Attempt Manager:  "+_name);
        System.out.println("    Attempt Number:  "+_attemptNum+" / "+_maxAttempts);
        System.out.println("--------------------------------------------");
        if(_terminated){ // If terminated by fatal error...
          System.out.println(" TERMINATING OPERATION:  This Attempt Manager");
          System.out.println("   has been instructed to terminate further  ");
          System.out.println("   operation at its level.  The error is     ");
          System.out.println("   unrecoverable without intervention at a   ");
          System.out.println("   higher level, such as by manual           ");
          System.out.println("   inspection.                               ");
          System.out.println("---------------------------------------------");
        }//end if(_terminate)_ // If terminated by fatal error...
        System.out.println(" REASON:  "+exc.getMessage());
        System.out.println("=============================================");
        System.out.println("");
        // Print Error
        SConnectDisconnectICM.printException(exc);
        System.out.println("");
        System.out.println("-------------------------");
        System.out.println("'"+(_maxAttempts-_attemptNum)+"' RETRY ATTEMPTS REMAIN");
        System.out.println("-------------------------");
        System.out.println("");

        // If terminated, rethrow
        if(_terminated){
            // Write message
            System.out.println("--------------------------------");
            System.out.println(" PROPAGATING ERROR (terminated)");
            System.out.println("--------------------------------");
            // Throw error
            throw(exc);
        }//end if(_termindated){
        // If there is a parent Attempt Manager, and it is in a retry attempt,
        // do not retry multiple times at this level.  This avoids a nxn retry
        // count.  Instead it is n+n.
        else if((_parentAttemptManager!=null) && _parentAttemptManager.isRetryAttempt()){
            // Write message
            System.out.println("------------------------------------------");
            System.out.println(" PROPAGATING ERROR (parent in retry mode) ");
            System.out.println("------------------------------------------");
            // Throw error
            throw(exc);
        }//end else if(_parentAttemptManager.isRetryAttempt()){
        // If there are more attempts left, print warning, delay if necessary, then move on.
        else if(_attemptNum < _maxAttempts){
            // Once so many attempts are passed, start delaying between retry attempts.
            if((_delayTimeMS > 0) && ((_attemptNum+1) >= _delayAtAttemptNum))
                waitFor(_delayTimeMS);
            // Move on.
        }//end if(_attemptNum < _maxAttempts){
        else{ // Otherwise write message & throw error.
            // Write message
            System.out.println("--------------------------");
            System.out.println(" NO RETRY ATTEMPTS REMAIN ");
            System.out.println("--------------------------");
            // Throw error
            throw(exc);
        }        
    }//end handleAttemptFailure()

   /**
    * Get the current attempt number.
    * @return Returns the current attempt number.
    **/
    public int getAttemptNum(){
        return(_attemptNum);
    }//end getAttemptNum()

   /**
    * Reports whether the operation that is was managing was reported
    * as completed.
    * @return Returns 'true' if this Attempt Manager was told that the operation
    *         that it was managing is complete.  Returns 'false' otherwise.
    **/
    public boolean isCompleted() throws Exception{
        return(_complete);
    }//end isCompleted()

   /**
    * Reports whether the current attempt is the first attempt, meaning that it is 
    * not on a retry attempt after a failure.  
    * @return Returns 'true' if this is the first attempt, 'false' otherwise.
    **/
    public boolean isFirstAttempt() throws Exception{
        boolean isFirstAttempt = false;
        if(_attemptNum == 1)
            isFirstAttempt = true;
        //Default: else
        //Default:     isLastAttempt = false;
        return(isFirstAttempt);
    }//end isFirstAttempt()

   /**
    * Reports whether the current attempt is the last attempt, meaning that it is
    * as at the maximum allowed retry attempts.
    * @return Returns 'true' if this is the maximum allowed attempt, 'false' otherwise.
    **/
    public boolean isLastAttempt() throws Exception{
        boolean isLastAttempt = false;
        if(_attemptNum == _maxAttempts)
            isLastAttempt = true;
        //Default: else
        //Default:     isLastAttempt = false;
        return(isLastAttempt);
    }//end isLastAttempt()

   /**
    * Reports whether the current attempt is a retry attempt, meaning that it is
    * not the first attempt.  If it is a reattempt, it means that the operation
    * in which it is controlling has errored out.  Until the managed operation
    * completes successfully once, it will remain as retry attempts.
    * @return Returns 'true' if this is not the first attempt, false otherwise.
    **/
    public boolean isRetryAttempt() throws Exception{
        boolean isRetryAttempt = false;
        if(_attemptNum > 1)
            isRetryAttempt = true;
        //Default: else
        //Default:     isRetryAttempt = false;
        return(isRetryAttempt);
    }//end isRetryAttempt()

   /**
    * Reports whether the operation that is was managing was terminated.
    * @return Returns 'true' if this Attempt Manager was told that the operation
    *         that it was managing should be terminated.  Returns 'false' otherwise.
    **/
    public boolean isTerminated() throws Exception{
        return(_terminated);
    }//end isTerminated()

   /** 
    * Request to start the next attempt.  If there are no attempts left
    * or if the operation was completed, this will return false.  Otherwise
    * the next attempt is started and 'true' is returned.
    * @return Returns 'true' if there are more attempts, 'false' if the
    *         operation should not move on.
    **/
    public boolean next(){
        boolean canPerformNext = false;
        // Don't continue if already completed.
        if(_complete)                       canPerformNext = false;
        // Don't continue if terminated.
        else if(_terminated)                canPerformNext = false;
        // if there are attempts remaining, continue
        else if(_attemptNum < _maxAttempts) canPerformNext = true;
        else                                canPerformNext = false;
        
        // If we are continuing, print notice & continue
        if(canPerformNext){
            // Continue
            _attemptNum++;
            // Print Notice
            System.out.println("------------------------------");   
            System.out.println(" STARTING ATTEMPT '"+_attemptNum+"' / '"+_maxAttempts+"'.");   
            System.out.println("------------------------------");   
        }
        return(canPerformNext);        
    }//end next()

   /**
    * If debug printing is enabled in the Export Options, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param debugMessage = Debug message to print if debug printing enabled.
    **/
    private void printDebug(String debugMessage){
        if(    (_options==null)
            || (_options.getPrintDebugEnable())
          ){
            System.out.println(debugMessage);   
        }
    }//end printDebug

   /**
    * If trace printing is enabled in the Export Options, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param traceMessage = Trace message to print if debug printing enabled.
    **/
    private void printTrace(String traceMessage){
        if(    (_options==null)
            || (_options.getPrintTraceEnable())
          ){
            System.out.println(traceMessage);   
        }
    }//end printTrace

   /** 
    * Reset the counter so that it believes no attempts have yet been made.
    * This method should be called prior to starting new restartable 
    * sequence.
    **/
    public void reset(){
        _attemptNum = 0;
        _complete   = false;
        _terminated = false;
        printDebug("--- Clearing / Reseting Attempt Manager: "+_name);
    }//end reset()

   /**
    * Tell the AttemptManager that the operation that it was managing completed
    * successfully.  The next() operation will fail on the next call.
    **/
    public void setComplete() throws Exception{
        _complete = true;
        // report message
        System.out.println("---------------------");
        System.out.println(" OPERATION COMPLETED ");
        System.out.println("---------------------");
    }//end setComplete()

   /**
    * Tell the AttemptManager that the operation that it was managing is fatally
    * errored and should not be restarted without manual intervention.
    * @param reason - Explanation for the unrecoverable error to report to the user.
    **/
    public void terminateAndThrow(String reason) throws Exception{
        _terminated = true;
        // report message
        System.out.println("");
        System.out.println("--- Terminate Operation Requested:");
        System.out.println("");
        System.out.println("=============================================");
        System.out.println("   Attempt Manager:  "+_name);
        System.out.println("    Attempt Number:  "+_attemptNum+" / "+_maxAttempts);
        System.out.println("---------------------------------------------");
        System.out.println(" TERMINATING OPERATION:  This Attempt Manager");
        System.out.println("   has been instructed to terminate further  ");
        System.out.println("   operation at its level.  The error is     ");
        System.out.println("   unrecoverable without intervention at a   ");
        System.out.println("   higher level, such as by manual           ");
        System.out.println("   inspection.                               ");
        System.out.println("---------------------------------------------");
        System.out.println(" REASON:  "+reason);
        System.out.println("=============================================");
        // Terminate Parent
        if(_parentAttemptManager!=null)
            _parentAttemptManager.terminateAndThrow(reason);
        // Throw Error
        throw new Exception(reason);
    }//end handleAttemptFailure()

   /**
    * Wait for the specified number of milliseconds
    * @param timeMS - Number of milliseconds to wait.
    **/
    public static void waitFor(long timeMS){
        System.out.println("");   
        System.out.println(" ******************** --------------------");   
        System.out.println(" *** WAIT REQUEST *** Delay: " + timeMS + " (ms)");   
        System.out.println(" ******************** --------------------");   
		//System.out.println(" Delay: " + timeMS + " (ms)");
		//System.out.println(" -------------------- ");
        System.out.println("");   

        long startTimeMS  = System.currentTimeMillis();
        long expireTimeMS = startTimeMS + timeMS;
        long secondsLeft  = (expireTimeMS - startTimeMS)/1000;
        long minutesLeft  = secondsLeft/60;
        java.sql.Timestamp startTS  = new java.sql.Timestamp(startTimeMS);
        java.sql.Timestamp expireTS = new java.sql.Timestamp(expireTimeMS);

        System.out.println("    Wait Loop Start Time:  "+startTimeMS  +" (ms)");
        System.out.println("                Duration: +"+timeMS       +" (ms)");
        System.out.println("                          -------------------------");
        System.out.println("            Wait Expires: +"+expireTimeMS +" (ms)");
        System.out.println("");   
        System.out.println("                  ~Start:  "+ startTS);
        System.out.println("                    ~End:  "+ expireTS);
        System.out.println("              ~Time Left:  "+secondsLeft  +" (s)");
        System.out.println("");

        long lastMinutesLeftPrinted = minutesLeft + 1;
        long lastSecondsLeftPrinted = secondsLeft + 1;
        for(long curTimeMS = System.currentTimeMillis();
            expireTimeMS >= curTimeMS;
            curTimeMS = System.currentTimeMillis()){
                
            secondsLeft = (expireTimeMS - curTimeMS)/1000;
            minutesLeft = secondsLeft / 60;
            
            if(secondsLeft > 60){ // when more than 1 min left, update status each minute.
                if(minutesLeft < lastMinutesLeftPrinted){
                    lastMinutesLeftPrinted = minutesLeft;
                    System.out.println("Time Left (until "+expireTS+"):  "+minutesLeft+" (min)");
                }//end if(minutesLeft < lastMinutesLeftPrinted){
            } else { // otherwise update every second tick.
                if(secondsLeft < lastSecondsLeftPrinted){
                    lastSecondsLeftPrinted = secondsLeft;
                    System.out.println("Time Left (until "+expireTS+"):  "+secondsLeft+" (s)");
                }//end if(secondsLeft < lastSecondsLeftPrinted){
            }//end else of if(secondsLeft > 60){
        }//end for(long curTime = System.currentTimeMillis();
        startTS  = null; // Free Memory
        expireTS = null;
    }//end waitFor()
   
}//end class TExportManagerICM_AttemptManager
              
