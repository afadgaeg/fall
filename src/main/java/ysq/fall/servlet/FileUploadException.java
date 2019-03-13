package ysq.fall.servlet;

/**
 * Thrown when an exception occurs while uploading a file. 
 *  
 * @author Shane Bryzak
 */
public class FileUploadException extends RuntimeException
{   
    /**
     *
     */
    public FileUploadException()
   {
      this(null, null);
   }
   
   /**
    *
    * @param message
    */
   public FileUploadException(String message)
   {
      this(message, null);
   }
   
   /**
    *
    * @param message
    * @param cause
    */
   public FileUploadException(String message, Throwable cause)
   {
      super(message, cause);
   }   
}
