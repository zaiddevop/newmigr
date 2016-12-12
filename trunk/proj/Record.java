/** ***************************************************
 * File:  Record.java
 *
 *
 * Copyright (c) 2002-2003 Advanced Applications Total Applications Works.
 * (AATAW)  All Rights Reserved.
 *
 * AATAW grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to AATAW.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. AATAW AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL AATAW OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 ***************************************************   */

import javax.swing.JOptionPane;
import java.io.*;
import javax.swing.*;
import java.util.*;

   /** *******************************************************************
    * class: The Record class' purpose is to read and write records to
    *        a randomaccess file.
    * *******************************************************************/
public class Record  {
   private int recID ;
   private int quantity ;
   private String toolType = "" ;
   private String brandName = ""  ;
   private String toolDesc  = "" ;
   private String partNum = ""  ;
   private String cost = ""  ;
   private String recordTokens[] ;
   private boolean myDebug = false ;
   private long filePos ;
   private long fileLen ;

   /** *******************************************************************
    *  Method: ReadRec() Reads a record from the specified RandomAccessFile.
    * 1- Read the first integer
    * 2- Read the second integer
    * 3- Read characters one at a time until we reach a string of
    *    ';;;'. This indicates that we have reached the end of the
    *    character string for this particular record.
    * 4- Load the resulting string into a StringTokenizer object.
    * 5- We are looking for 7 tokens, so if the token count is
    *    greater than 4, we will tokenize the string.
    * 6- The tokens are loaded into a string array and then into the
    *    class variables.
    ********************************************************** */

   public void ReadRec( RandomAccessFile file ) throws IOException
   {
      char f[] = new char[ 585 ], ch;
      StringTokenizer tokens ;
      String str = "", str2 = "" ;
      StringBuffer buf1  = new StringBuffer("");
      int ii = 0 , loopCtl = 585 , len = 0 ;
      long remm = fileLen - filePos ;


      sysPrint( "ReadRec() 1a: Remaining bytes is " + remm ) ;

      sysPrint( "ReadRec() 1b: Reading ints" ) ;

      recID    = file.readInt();
      sysPrint( "ReadRec() 1c: recID  is " +  recID) ;

      quantity = file.readInt();

      sysPrint( "ReadRec() 2: Reading string" ) ;

      /** Read characters until we get to ;;; which
       *  indicates the end of the record */
      while ( ii < loopCtl ) {
         ch =  file.readChar() ;
         str = str + ch  ;
         len = str.length() ;

         if ( ii > 4 ) {
            str2 = str.substring( len-4 , len-1 ) ;
            if ( str2.equals( ";;;" ) )
                break ;
         }

         ii++ ;
      }

      sysPrint( "ReadRec() 3a: str is " + str ) ;

      sysPrint( "ReadRec() 3b: Reading string. ii =s " + ii ) ;

      sysPrint(  "ReadRec() 4: The value of str is " + str ) ;

      tokens = new StringTokenizer( str , ";;" ) ;


      recordTokens = new String[ 7 ] ;


      if ( tokens.countTokens() >= 4 )   {
         sysPrint(  "ReadRec() 5: The number of tokens is " +
                   tokens.countTokens() ) ;
         ii = 2 ;

         /** Load the tokens into a string array. */
         while( ii < 7  )  {
            recordTokens[ ii ] = tokens.nextToken().toString() ;
            ii++ ;
         }


         toolType  = new String( recordTokens[ 2 ] ) ;
         brandName = new String( recordTokens[ 3 ] ) ;
         partNum   = new String( recordTokens[ 4 ] ) ;
         cost      = new String( recordTokens[ 5 ] ) ;
         toolDesc  = new String( recordTokens[ 6 ] ) ;
      }
      else  {
         sysPrint(  "ReadRec() 6: There are no records to read." );
      }      //  end of if
   }

   /** ********************************************************
    *  The fill() method is used to fill in the passed string with
    *  blanks.
    ******************************************************** */
    public StringBuffer fill ( String str, StringBuffer buf , int len) {
       String strTwo = new String( "                     "  +
           "                                             " ) ;

       if ( str != null ) {
          buf.setLength( len );
          buf = new StringBuffer( str + strTwo );
       }
       else
          buf = new StringBuffer( strTwo );

       if ( len == 0 ) {
          buf.setLength( 45 );
       }
       else {
          buf.setLength( len );
       }

      return buf ;
   }

   /** ************************************************************
    *  write() Writes a record to the specified RandomAccessFile.
    *          1- First it writes a int (recid) to the output file
    *          2- Next it writes the quantity as an int.
    *          3- Then it writes the remaing record as a string.
    ************************************************************** */
   public void write( RandomAccessFile file ) throws IOException
   {
      StringBuffer buf  = new StringBuffer( " " );
      String str = "" , str2 = ""  ;

      file.writeInt( recID );

      file.writeInt( quantity );

      str = str + toolType + ";;" ;

      str = str + brandName + ";;"  ;
      str = str + partNum + ";;"  ;
      str = str + cost  + ";;" ;

      str = str + toolDesc + ";;;"  ;


      buf = fill ( str , buf.delete(0, 451), 451 ) ;

      file.writeChars( buf.toString() );
      sysPrint(  "write(): - The value of recID is " + recID );
      sysPrint(  "write(): - The value of quantity is " + quantity );
      sysPrint(  "write(): - The value of str2 is " + str + " with a length of "
                   + str2.length() );
      sysPrint(  "write(): - The length of buf is " + buf.length() );

   }


   /** ********************************************************
    * Method: writeInteger() is used to wite an integer to the
    *         randomaccess file.
    ********************************************************/
   public void writeInteger( RandomAccessFile file , int a ) throws IOException
   {

      file.writeInt( a );
   }


   /** ********************************************************
    * Method: getRecID() is used to obtain the record ID.
    ********************************************************/
   public int getRecID() { return recID; }


   /** ********************************************************
    * Method: getToolType() is used to obtain the tool type.
    ********************************************************/
   public String getToolType() { return toolType.trim(); }

   /** ********************************************************
    * Method: getToolDesc() is used to obtain the description of
    *         the tool.
    ********************************************************/
   public String getToolDesc() { return toolDesc.trim(); }

   /** ********************************************************
    * Method: getPartNumber() is used to obtain the Part Number
    *         value currently in the record.
    ********************************************************/
   public String getPartNumber() { return partNum.trim(); }

   /** ********************************************************
    * Method: getQuantity() is used to obtain the value of the
    *         quantity currently in the record.
    ********************************************************/
   public int getQuantity() { return quantity; }

   /** ********************************************************
    * Method: getBrandName() is used to obtain the value of the
    *         current value of Brand Name from the record.
    ********************************************************/
   public String getBrandName() { return brandName.trim(); }

   /** ********************************************************
    * Method: getCost() is used to obtain the the value of the
    *         current value of cost  from the record.
    ********************************************************/
   public String getCost() { return cost.trim(); }

   /** ********************************************************
    * Method: setToolType() is used to set the value of the
    *         current value of Tool Type from the record.
    ********************************************************/
   public void setToolType( String f ) { toolType = f; }

   /** ********************************************************
    * Method: setRecID() is used to set the value of the of the
    *         record ID in the record.
    ********************************************************/
   public void setRecID( int p ) { recID = p; }

   /** ********************************************************
    * Method: setCost() is used to set the value of the of the
    *         cost in the record.
    ********************************************************/
   public void setCost( String f ) { cost = f; }

   /** ********************************************************
    * Method: setBrandName() is used to set the value of the
    *         brand name in the record.
    ********************************************************/
   public void setBrandName( String f ) { brandName = f; }

   /** *************************************************************
    * Method: setToolDesc() is used to set the value of the tool
    *         description in the record.
    ********************************************************/
   public void setToolDesc( String f ) { toolDesc = f; }

   /** ********************************************************
    * Method: setPartNumber() is used to set the part number
    *         in the record.
    ********************************************************/
   public void setPartNumber( String f ) { partNum = f; }

   /** ********************************************************
    * Method: setQuantity() is used to set the value of the
    *         quantity in the record.
    ********************************************************/
   public void setQuantity( int q ) { quantity = q; }

   /** ********************************************************
    * Method: setFilePos() is used to set the current position
    *         of the cursor in the file.
    ********************************************************/
   public void setFilePos( long fp ) { filePos = fp; }

   /** *******************************************************************
    * Method: setFileLen() is used to set the current length of the file.
    **********************************************************************/
   public void setFileLen( long fl ) { fileLen = fl; }

   /** ********************************************************
    * Method: sysPrint() is a debugging aid that is used to print
    *          information to the screen.
    ********************************************************/
   public void sysPrint( String str  )   {
      if ( myDebug ) {
         System.out.println( str );
      }
   }


   /** ******************************************************************
    * NOTE: This method contains a hard coded value for the
    * size of a record of information. The value is arrived at
    * by adding up the size Java allocates to each data type
    * writeChars(String s)
    *   Writes every character in the string s, to the output stream, in
    *   order, two bytes per character.
    * The data Record is five strings of 45 characters each and two int
    * data types.
    *
    ***************************************************************** */
   public static int getSize() { return 585; }
}
