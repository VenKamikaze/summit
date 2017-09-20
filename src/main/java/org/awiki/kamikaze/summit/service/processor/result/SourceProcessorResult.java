package org.awiki.kamikaze.summit.service.processor.result;

public class SourceProcessorResult
{
  public static long STANDARD_SUCCESS_CODE = 0;
  public static String STANDARD_SUCCESS_MESSAGE = "Success";
  
  public static long STANDARD_NO_RESULT_CODE = -1;
  public static String STANDARD_NO_RESULT_MESSAGE = "No data found.";
  
  private Long rowsAffected;
  private long returnCode;
  private String outputMessage;
  private String resultValue; // all DML queries are cast to String, TODO change to convert to appropriate type
  
  public SourceProcessorResult(Long rowsAffected, long returnCode, String outputMessage, String resultValue)
  {
    this.rowsAffected = rowsAffected;
    this.returnCode = returnCode;
    this.outputMessage = outputMessage;
    this.resultValue = resultValue;
  }
  
  public SourceProcessorResult()
  {  }

  public Long getRowsAffected()
  {
    return rowsAffected;
  }
  public void setRowsAffected(long rowsAffected)
  {
    this.rowsAffected = rowsAffected;
  }
  public long getReturnCode()
  {
    return returnCode;
  }
  public void setReturnCode(long returnCode)
  {
    this.returnCode = returnCode;
  }
  public String getOutputMessage()
  {
    return outputMessage;
  }
  public void setOutputMessage(String outputMessage)
  {
    this.outputMessage = outputMessage;
  }

  public String getResultValue()
  {
    return resultValue;
  }

  public void setResultValue(String resultValue)
  {
    this.resultValue = resultValue;
  }
  
  
}
