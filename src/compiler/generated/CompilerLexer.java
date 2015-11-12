/* The following code was generated by JFlex 1.4.2 on 13/11/15 00:48 */

package compiler.generated;
import java_cup.runtime.*;
import compiler.Compilation;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.2
 * on 13/11/15 00:48 from the specification file
 * <tt>C:/Users/petrh/compilation/lexer/compiler.jflex</tt>
 */
public class CompilerLexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int LONG_COMMENT = 4;
  public static final int STRING = 2;
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2, 2
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\70\1\63\2\0\1\64\22\0\1\70\1\17\1\65\2\0"+
    "\1\13\1\20\1\67\1\32\1\33\1\11\1\23\1\36\1\10\1\6"+
    "\1\12\1\3\11\2\1\34\1\35\1\14\1\15\1\16\1\0\1\25"+
    "\6\5\24\1\1\26\1\66\1\27\1\22\1\62\1\0\1\53\1\55"+
    "\1\42\1\43\1\7\1\45\1\61\1\57\1\50\1\1\1\56\1\51"+
    "\1\1\1\46\1\41\1\37\1\1\1\40\1\52\1\47\1\44\1\1"+
    "\1\60\1\4\1\54\1\1\1\30\1\21\1\31\1\24\uff81\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\1\2\0\1\2\1\3\2\1\1\4\1\3\1\5"+
    "\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25"+
    "\1\26\1\27\1\30\1\31\1\32\1\33\15\3\1\34"+
    "\1\35\1\36\1\37\1\40\1\41\1\42\1\37\2\43"+
    "\1\44\1\0\1\45\1\3\1\46\1\47\1\50\1\51"+
    "\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61"+
    "\1\62\1\63\1\64\1\65\1\66\1\67\1\70\1\71"+
    "\1\72\1\73\2\3\1\74\11\3\1\75\1\76\4\3"+
    "\2\0\1\77\1\100\1\101\1\102\1\44\1\0\1\3"+
    "\2\52\1\103\1\104\5\3\1\105\2\3\1\106\10\3"+
    "\1\107\4\0\1\107\1\110\10\3\1\111\1\112\1\113"+
    "\1\114\1\3\1\115\2\3\1\116\1\117\1\120\1\121"+
    "\1\122\3\3\1\123\3\3\1\124\1\3\1\125\1\126"+
    "\1\3\1\127\1\130\6\3\1\131\4\3\1\132\1\3"+
    "\1\133\1\3\1\134\13\3\1\135";

  private static int [] zzUnpackAction() {
    int [] result = new int[199];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\71\0\162\0\253\0\344\0\u011d\0\u0156\0\u018f"+
    "\0\u01c8\0\u0201\0\u023a\0\u0273\0\u02ac\0\u02e5\0\u031e\0\u0357"+
    "\0\u0390\0\u03c9\0\u0402\0\u043b\0\u0474\0\253\0\253\0\u04ad"+
    "\0\u04e6\0\253\0\253\0\253\0\253\0\253\0\253\0\253"+
    "\0\u051f\0\u0558\0\u0591\0\u05ca\0\u0603\0\u063c\0\u0675\0\u06ae"+
    "\0\u06e7\0\u0720\0\u0759\0\u0792\0\u07cb\0\253\0\253\0\u0804"+
    "\0\253\0\253\0\253\0\253\0\u083d\0\253\0\u0876\0\u08af"+
    "\0\u08e8\0\u0921\0\u095a\0\253\0\253\0\253\0\253\0\u0993"+
    "\0\253\0\253\0\u09cc\0\253\0\253\0\253\0\u0a05\0\253"+
    "\0\253\0\253\0\253\0\253\0\253\0\253\0\253\0\253"+
    "\0\253\0\u0a3e\0\u0a77\0\344\0\u0ab0\0\u0ae9\0\u0b22\0\u0b5b"+
    "\0\u0b94\0\u0bcd\0\u0c06\0\u0c3f\0\u0c78\0\344\0\344\0\u0cb1"+
    "\0\u0cea\0\u0d23\0\u0d5c\0\u0d95\0\u0dce\0\253\0\253\0\253"+
    "\0\253\0\u0e07\0\u0e07\0\u0e40\0\253\0\u0e79\0\253\0\253"+
    "\0\u0eb2\0\u0eeb\0\u0f24\0\u0f5d\0\u0f96\0\u0fcf\0\u1008\0\u1041"+
    "\0\344\0\u107a\0\u10b3\0\u10ec\0\u1125\0\u115e\0\u1197\0\u11d0"+
    "\0\u1209\0\253\0\u1242\0\u127b\0\u12b4\0\u12ed\0\u1326\0\344"+
    "\0\u135f\0\u1398\0\u13d1\0\u140a\0\u1443\0\u147c\0\u14b5\0\u14ee"+
    "\0\344\0\344\0\344\0\344\0\u1527\0\344\0\u1560\0\u1599"+
    "\0\253\0\253\0\253\0\253\0\253\0\u15d2\0\u160b\0\u1644"+
    "\0\344\0\u167d\0\u16b6\0\u16ef\0\344\0\u1728\0\344\0\344"+
    "\0\u1761\0\344\0\344\0\u179a\0\u17d3\0\u180c\0\u1845\0\u187e"+
    "\0\u18b7\0\344\0\u18f0\0\u1929\0\u1962\0\u199b\0\344\0\u19d4"+
    "\0\344\0\u1a0d\0\344\0\u1a46\0\u1a7f\0\u1ab8\0\u1af1\0\u1b2a"+
    "\0\u1b63\0\u1b9c\0\u1bd5\0\u1c0e\0\u1c47\0\u1c80\0\344";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[199];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\1\6\1\7\2\5\1\10\1\11\1\12"+
    "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22"+
    "\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\1\45\1\5\1\46\1\47\1\50\1\51"+
    "\1\52\1\53\2\5\1\54\2\5\1\55\2\5\2\56"+
    "\1\57\1\4\1\60\1\56\63\61\1\62\1\63\1\64"+
    "\1\65\2\61\11\66\1\67\57\66\72\0\5\5\1\0"+
    "\1\5\27\0\24\5\10\0\2\6\2\0\1\70\1\71"+
    "\63\0\2\6\1\72\1\0\1\70\1\71\63\0\2\70"+
    "\66\0\5\5\1\0\1\5\27\0\12\5\1\73\11\5"+
    "\16\0\1\74\4\0\1\75\70\0\1\76\64\0\1\77"+
    "\1\100\2\0\1\101\70\0\1\102\67\0\1\103\1\104"+
    "\70\0\1\105\70\0\1\106\1\107\67\0\1\110\70\0"+
    "\1\111\2\0\1\112\65\0\1\113\3\0\1\114\64\0"+
    "\1\115\70\0\1\116\5\0\1\117\73\0\1\120\71\0"+
    "\1\121\42\0\5\5\1\0\1\5\27\0\1\5\1\122"+
    "\22\5\7\0\5\5\1\0\1\123\27\0\24\5\7\0"+
    "\5\5\1\0\1\5\27\0\6\5\1\124\15\5\7\0"+
    "\5\5\1\0\1\5\27\0\12\5\1\125\11\5\7\0"+
    "\5\5\1\0\1\126\27\0\24\5\7\0\5\5\1\0"+
    "\1\5\27\0\2\5\1\127\2\5\1\130\6\5\1\131"+
    "\7\5\7\0\5\5\1\0\1\132\27\0\5\5\1\133"+
    "\16\5\7\0\5\5\1\0\1\5\27\0\1\5\1\134"+
    "\13\5\1\135\6\5\7\0\5\5\1\0\1\5\27\0"+
    "\6\5\1\136\1\137\14\5\7\0\5\5\1\0\1\5"+
    "\27\0\11\5\1\140\12\5\7\0\5\5\1\0\1\5"+
    "\27\0\10\5\1\141\13\5\7\0\5\5\1\0\1\5"+
    "\27\0\1\5\1\142\22\5\7\0\5\5\1\0\1\5"+
    "\27\0\20\5\1\143\3\5\6\0\63\144\1\0\2\144"+
    "\1\145\2\144\40\146\1\63\5\146\1\62\14\146\1\0"+
    "\1\146\1\147\1\150\2\146\12\0\1\151\60\0\2\70"+
    "\3\0\1\71\63\0\2\152\4\0\1\153\62\0\2\72"+
    "\1\0\1\72\64\0\5\5\1\0\1\5\27\0\13\5"+
    "\1\154\10\5\6\0\63\100\1\155\1\156\4\100\15\0"+
    "\1\157\70\0\1\160\54\0\5\5\1\0\1\5\27\0"+
    "\2\5\1\161\21\5\7\0\5\5\1\0\1\5\27\0"+
    "\1\162\7\5\1\163\13\5\7\0\5\5\1\0\1\5"+
    "\27\0\14\5\1\164\7\5\7\0\5\5\1\0\1\5"+
    "\27\0\16\5\1\165\5\5\7\0\5\5\1\0\1\5"+
    "\27\0\1\5\1\166\22\5\7\0\5\5\1\0\1\5"+
    "\27\0\7\5\1\167\14\5\7\0\5\5\1\0\1\5"+
    "\27\0\12\5\1\170\11\5\7\0\5\5\1\0\1\5"+
    "\27\0\21\5\1\171\2\5\7\0\5\5\1\0\1\5"+
    "\27\0\12\5\1\172\11\5\7\0\5\5\1\0\1\5"+
    "\27\0\5\5\1\173\16\5\7\0\5\5\1\0\1\5"+
    "\27\0\1\174\23\5\7\0\5\5\1\0\1\5\27\0"+
    "\13\5\1\175\10\5\7\0\5\5\1\0\1\5\27\0"+
    "\1\5\1\176\1\177\21\5\7\0\5\5\1\0\1\200"+
    "\27\0\24\5\7\0\5\5\1\0\1\5\27\0\11\5"+
    "\1\201\12\5\75\0\1\202\41\0\1\203\5\0\1\204"+
    "\16\0\1\205\1\206\1\207\3\0\2\152\66\0\5\5"+
    "\1\0\1\210\27\0\24\5\71\0\1\155\6\0\5\5"+
    "\1\0\1\5\27\0\3\5\1\211\20\5\7\0\5\5"+
    "\1\0\1\212\27\0\24\5\7\0\5\5\1\0\1\5"+
    "\27\0\5\5\1\213\16\5\7\0\5\5\1\0\1\5"+
    "\27\0\13\5\1\214\10\5\7\0\5\5\1\0\1\5"+
    "\27\0\5\5\1\215\16\5\7\0\5\5\1\0\1\216"+
    "\27\0\24\5\7\0\5\5\1\0\1\5\27\0\3\5"+
    "\1\217\20\5\7\0\5\5\1\0\1\5\27\0\13\5"+
    "\1\220\10\5\7\0\5\5\1\0\1\5\27\0\12\5"+
    "\1\221\11\5\7\0\5\5\1\0\1\222\27\0\24\5"+
    "\7\0\5\5\1\0\1\223\27\0\24\5\7\0\5\5"+
    "\1\0\1\5\27\0\10\5\1\224\13\5\7\0\5\5"+
    "\1\0\1\5\27\0\5\5\1\225\16\5\7\0\5\5"+
    "\1\0\1\5\27\0\1\226\23\5\7\0\5\5\1\0"+
    "\1\5\27\0\14\5\1\227\7\5\7\0\5\5\1\0"+
    "\1\5\27\0\12\5\1\230\11\5\75\0\1\231\70\0"+
    "\1\232\70\0\1\233\70\0\1\234\70\0\1\235\2\0"+
    "\5\5\1\0\1\236\27\0\24\5\7\0\5\5\1\0"+
    "\1\5\27\0\14\5\1\237\7\5\7\0\5\5\1\0"+
    "\1\5\27\0\1\5\1\240\22\5\7\0\5\5\1\0"+
    "\1\5\27\0\13\5\1\241\10\5\7\0\5\5\1\0"+
    "\1\5\27\0\22\5\1\242\1\5\7\0\5\5\1\0"+
    "\1\5\27\0\14\5\1\243\7\5\7\0\5\5\1\0"+
    "\1\5\27\0\10\5\1\244\13\5\7\0\5\5\1\0"+
    "\1\245\27\0\24\5\7\0\5\5\1\0\1\5\27\0"+
    "\3\5\1\246\20\5\7\0\5\5\1\0\1\5\27\0"+
    "\17\5\1\247\4\5\7\0\5\5\1\0\1\250\27\0"+
    "\24\5\7\0\5\5\1\0\1\5\27\0\4\5\1\251"+
    "\17\5\7\0\5\5\1\0\1\5\27\0\10\5\1\252"+
    "\13\5\7\0\5\5\1\0\1\5\27\0\7\5\1\253"+
    "\14\5\7\0\5\5\1\0\1\5\27\0\23\5\1\254"+
    "\7\0\5\5\1\0\1\5\27\0\3\5\1\255\20\5"+
    "\7\0\5\5\1\0\1\5\27\0\11\5\1\256\12\5"+
    "\7\0\5\5\1\0\1\5\27\0\10\5\1\257\13\5"+
    "\7\0\5\5\1\0\1\5\27\0\5\5\1\260\16\5"+
    "\7\0\5\5\1\0\1\5\27\0\13\5\1\261\10\5"+
    "\7\0\5\5\1\0\1\5\27\0\20\5\1\262\3\5"+
    "\7\0\5\5\1\0\1\5\27\0\2\5\1\263\21\5"+
    "\7\0\5\5\1\0\1\5\27\0\5\5\1\264\16\5"+
    "\7\0\5\5\1\0\1\5\27\0\1\5\1\265\22\5"+
    "\7\0\5\5\1\0\1\5\27\0\1\266\23\5\7\0"+
    "\5\5\1\0\1\5\27\0\7\5\1\267\14\5\7\0"+
    "\5\5\1\0\1\5\27\0\1\5\1\270\22\5\7\0"+
    "\5\5\1\0\1\271\27\0\24\5\7\0\5\5\1\0"+
    "\1\5\27\0\14\5\1\272\7\5\7\0\5\5\1\0"+
    "\1\273\27\0\24\5\7\0\5\5\1\0\1\5\27\0"+
    "\21\5\1\274\2\5\7\0\5\5\1\0\1\5\27\0"+
    "\7\5\1\275\14\5\7\0\5\5\1\0\1\5\27\0"+
    "\23\5\1\276\7\0\5\5\1\0\1\5\27\0\13\5"+
    "\1\277\10\5\7\0\5\5\1\0\1\5\27\0\10\5"+
    "\1\300\13\5\7\0\5\5\1\0\1\5\27\0\1\5"+
    "\1\301\22\5\7\0\5\5\1\0\1\5\27\0\5\5"+
    "\1\302\16\5\7\0\5\5\1\0\1\5\27\0\3\5"+
    "\1\303\20\5\7\0\5\5\1\0\1\5\27\0\10\5"+
    "\1\304\13\5\7\0\5\5\1\0\1\5\27\0\5\5"+
    "\1\305\16\5\7\0\5\5\1\0\1\5\27\0\1\5"+
    "\1\306\22\5\7\0\5\5\1\0\1\307\27\0\24\5"+
    "\6\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[7353];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\1\2\0\1\11\21\1\2\11\2\1\7\11\15\1"+
    "\2\11\1\1\4\11\1\1\1\11\2\1\1\0\2\1"+
    "\4\11\1\1\2\11\1\1\3\11\1\1\12\11\22\1"+
    "\2\0\4\11\1\1\1\0\1\1\1\11\1\1\2\11"+
    "\21\1\1\11\4\0\22\1\5\11\52\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[199];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
public Compilation compilation;
private String stringBeingBuilt;
private String characterBeingBuilt;

private Symbol symbol (int type) {
        return new Symbol (type, yyline, yycolumn);
}

private Symbol symbol (int type, Object value) {
        return new Symbol (type, yyline, yycolumn, value);
}


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public CompilerLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public CompilerLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 138) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 63: 
          { compilation.lexicalError("Unrecognized escape sequence '" + yytext() + "' within a string.", yyline, yycolumn);
          }
        case 94: break;
        case 69: 
          { return symbol(CompilerSymbol.FOR);
          }
        case 95: break;
        case 14: 
          { return symbol(CompilerSymbol.BITWISEOR);
          }
        case 96: break;
        case 3: 
          { return symbol(CompilerSymbol.IDENTIFIER, yytext());
          }
        case 97: break;
        case 59: 
          { return symbol(CompilerSymbol.RIGHTTYPEBRACKET);
          }
        case 98: break;
        case 12: 
          { return symbol(CompilerSymbol.LOGICALNEGATION);
          }
        case 99: break;
        case 26: 
          { return symbol(CompilerSymbol.SEMICOLON);
          }
        case 100: break;
        case 58: 
          { return symbol(CompilerSymbol.LEFTTYPEBRACKET);
          }
        case 101: break;
        case 64: 
          { stringBeingBuilt += "\"";
          }
        case 102: break;
        case 71: 
          { return symbol(CompilerSymbol.CHARACTER,  yytext().toCharArray()[1]);
          }
        case 103: break;
        case 72: 
          { return symbol(CompilerSymbol.ELSE);
          }
        case 104: break;
        case 70: 
          { return symbol(CompilerSymbol.NEW);
          }
        case 105: break;
        case 29: 
          { yybegin(STRING); stringBeingBuilt = new String();
          }
        case 106: break;
        case 38: 
          { return symbol(CompilerSymbol.DECREMENT);
          }
        case 107: break;
        case 68: 
          { return symbol(CompilerSymbol.UNARYSHIFTRIGHT);
          }
        case 108: break;
        case 51: 
          { return symbol(CompilerSymbol.UNARYBITWISEAND);
          }
        case 109: break;
        case 5: 
          { return symbol(CompilerSymbol.MINUS);
          }
        case 110: break;
        case 61: 
          { return symbol(CompilerSymbol.IF);
          }
        case 111: break;
        case 10: 
          { return symbol(CompilerSymbol.EQUAL);
          }
        case 112: break;
        case 1: 
          { int number;
    try {
       number = Integer.parseInt(yytext());
    } catch (NumberFormatException ex) {
       number = 0;
       compilation.lexicalError("The integer '" + yytext() + "' is too large to fit in 32 bits.", yyline, yycolumn);
    }
    return symbol(CompilerSymbol.INTEGER,number );
          }
        case 113: break;
        case 18: 
          { return symbol(CompilerSymbol.CONCATENATE);
          }
        case 114: break;
        case 25: 
          { return symbol(CompilerSymbol.COLON);
          }
        case 115: break;
        case 47: 
          { return symbol(CompilerSymbol.EQUALEQUAL);
          }
        case 116: break;
        case 87: 
          { return symbol(CompilerSymbol.REPEAT);
          }
        case 117: break;
        case 36: 
          { float number = Float.parseFloat(yytext());
    if (Float.isInfinite(number)) {
        compilation.lexicalError("The number '" + yytext() + "' is too large to be represented as a floating-point number.", yyline, yycolumn);
    }
    if (number == 0) {
            compilation.lexicalError("The number '" + yytext() + "' is too small to be accurately represented as a floating-point number. Use '0' instead if that was the intention.", yyline, yycolumn);
    }
    return symbol(CompilerSymbol.FLOAT, number);
          }
        case 118: break;
        case 65: 
          { stringBeingBuilt += "\\";
          }
        case 119: break;
        case 34: 
          { yybegin(YYINITIAL); 
		 return symbol(CompilerSymbol.STRING, stringBeingBuilt);
          }
        case 120: break;
        case 76: 
          { return symbol(CompilerSymbol.LIST);
          }
        case 121: break;
        case 60: 
          { return symbol(CompilerSymbol.OF);
          }
        case 122: break;
        case 77: 
          { return symbol(CompilerSymbol.STOP);
          }
        case 123: break;
        case 11: 
          { return symbol(CompilerSymbol.GREATERTHAN);
          }
        case 124: break;
        case 22: 
          { return symbol(CompilerSymbol.RBRACE);
          }
        case 125: break;
        case 86: 
          { return symbol(CompilerSymbol.WHILE);
          }
        case 126: break;
        case 39: 
          { return symbol(CompilerSymbol.UNARYMINUS);
          }
        case 127: break;
        case 81: 
          { return symbol(CompilerSymbol.CHARACTER, '\\');
          }
        case 128: break;
        case 27: 
          { return symbol(CompilerSymbol.COMMA);
          }
        case 129: break;
        case 73: 
          { return symbol(CompilerSymbol.NULL);
          }
        case 130: break;
        case 92: 
          { return symbol(CompilerSymbol.STRUCTURE);
          }
        case 131: break;
        case 56: 
          { return symbol(CompilerSymbol.UNARYPLUS);
          }
        case 132: break;
        case 16: 
          { return symbol(CompilerSymbol.PLUS);
          }
        case 133: break;
        case 91: 
          { return symbol(CompilerSymbol.PROCEDURE);
          }
        case 134: break;
        case 54: 
          { return symbol(CompilerSymbol.LOGICALOR);
          }
        case 135: break;
        case 88: 
          { return symbol(CompilerSymbol.RETURN);
          }
        case 136: break;
        case 24: 
          { return symbol(CompilerSymbol.RPAR);
          }
        case 137: break;
        case 35: 
          { /* do nothing */
          }
        case 138: break;
        case 84: 
          { return symbol(CompilerSymbol.BOOLEAN, false);
          }
        case 139: break;
        case 55: 
          { return symbol(CompilerSymbol.UNARYXOR);
          }
        case 140: break;
        case 23: 
          { return symbol(CompilerSymbol.LPAR);
          }
        case 141: break;
        case 44: 
          { return symbol(CompilerSymbol.UNARYMODULO);
          }
        case 142: break;
        case 93: 
          { return symbol(CompilerSymbol.DEBUG_SPAWN_STRUCTURE);
          }
        case 143: break;
        case 21: 
          { return symbol(CompilerSymbol.LBRACE);
          }
        case 144: break;
        case 33: 
          { stringBeingBuilt += "\r";
          }
        case 145: break;
        case 67: 
          { return symbol(CompilerSymbol.UNARYSHIFTLEFT);
          }
        case 146: break;
        case 46: 
          { return symbol(CompilerSymbol.LESSOREQUAL);
          }
        case 147: break;
        case 15: 
          { return symbol(CompilerSymbol.XOR);
          }
        case 148: break;
        case 52: 
          { return symbol(CompilerSymbol.LOGICALAND);
          }
        case 149: break;
        case 50: 
          { return symbol(CompilerSymbol.UNEQUAL);
          }
        case 150: break;
        case 6: 
          { return symbol(CompilerSymbol.TIMES);
          }
        case 151: break;
        case 43: 
          { return symbol(CompilerSymbol.UNARYDIVIDE);
          }
        case 152: break;
        case 42: 
          { /* ignore line comments */
          }
        case 153: break;
        case 4: 
          { return symbol(CompilerSymbol.PERIOD);
          }
        case 154: break;
        case 8: 
          { return symbol(CompilerSymbol.MODULO);
          }
        case 155: break;
        case 32: 
          { stringBeingBuilt += "\n";
          }
        case 156: break;
        case 48: 
          { return symbol(CompilerSymbol.GREATEROREQUAL);
          }
        case 157: break;
        case 66: 
          { yybegin(YYINITIAL);
          }
        case 158: break;
        case 30: 
          { compilation.lexicalError("An apostrophe encountered that is not part of any valid character constant. Skipping.", yyline, yycolumn);
          }
        case 159: break;
        case 45: 
          { return symbol(CompilerSymbol.SHIFTLEFT);
          }
        case 160: break;
        case 49: 
          { return symbol(CompilerSymbol.SHIFTRIGHT);
          }
        case 161: break;
        case 13: 
          { return symbol(CompilerSymbol.BITWISEAND);
          }
        case 162: break;
        case 90: 
          { return symbol(CompilerSymbol.FUNCTION);
          }
        case 163: break;
        case 83: 
          { return symbol(CompilerSymbol.CLASS);
          }
        case 164: break;
        case 7: 
          { return symbol(CompilerSymbol.DIVIDE);
          }
        case 165: break;
        case 28: 
          { /* ignore whitespace */
          }
        case 166: break;
        case 17: 
          { return symbol(CompilerSymbol.BITWISENEGATION);
          }
        case 167: break;
        case 89: 
          { return symbol(CompilerSymbol.FOREACH);
          }
        case 168: break;
        case 9: 
          { return symbol(CompilerSymbol.LESSTHAN);
          }
        case 169: break;
        case 57: 
          { return symbol(CompilerSymbol.INCREMENT);
          }
        case 170: break;
        case 79: 
          { return symbol(CompilerSymbol.CHARACTER, '\n');
          }
        case 171: break;
        case 80: 
          { return symbol(CompilerSymbol.CHARACTER, '\"');
          }
        case 172: break;
        case 53: 
          { return symbol(CompilerSymbol.UNARYBITWISEOR);
          }
        case 173: break;
        case 19: 
          { return symbol(CompilerSymbol.LBRACKET);
          }
        case 174: break;
        case 40: 
          { return symbol(CompilerSymbol.UNARYTIMES);
          }
        case 175: break;
        case 62: 
          { return symbol(CompilerSymbol.IN);
          }
        case 176: break;
        case 31: 
          { stringBeingBuilt += yytext();
          }
        case 177: break;
        case 75: 
          { return symbol(CompilerSymbol.TYPE);
          }
        case 178: break;
        case 2: 
          { compilation.lexicalError("Unknown character '" + yytext() + "' encountered. Skipping.", yyline, yycolumn);
          }
        case 179: break;
        case 74: 
          { return symbol(CompilerSymbol.BOOLEAN, true);
          }
        case 180: break;
        case 37: 
          { int number;
	try {
       number = Integer.decode(yytext());
    } catch (NumberFormatException ex) {
        number = 0;
        compilation.lexicalError("The string '" + yytext() + "' doesn't represent a valid character.", yyline, yycolumn);
    }
    if (!Character.isDefined(number)) {
    	number = 0;
    	compilation.lexicalError("The string '" + yytext() + "' doesn't represent a valid character.", yyline, yycolumn);
    }
    char c = (char) number;
    return symbol(CompilerSymbol.CHARACTER, c);
          }
        case 181: break;
        case 78: 
          { return symbol(CompilerSymbol.CHARACTER, '\r');
          }
        case 182: break;
        case 20: 
          { return symbol(CompilerSymbol.RBRACKET);
          }
        case 183: break;
        case 41: 
          { yybegin(LONG_COMMENT);
          }
        case 184: break;
        case 85: 
          { return symbol(CompilerSymbol.BREAK);
          }
        case 185: break;
        case 82: 
          { return symbol(CompilerSymbol.CHARACTER, '\'');
          }
        case 186: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
            switch (zzLexicalState) {
            case LONG_COMMENT: {
              yybegin(YYINITIAL);
   compilation.lexicalError("End of file reached while parsing a multiline comment. You forgot to close it with '*/'.", yyline, yycolumn);
            }
            case 200: break;
            case STRING: {
              yybegin(YYINITIAL); 
	 	 compilation.lexicalError("End of file reached while parsing a string. You forgot to close the double quotes.", yyline, yycolumn);
	 	 return symbol(CompilerSymbol.STRING, stringBeingBuilt);
            }
            case 201: break;
            default:
              { return new java_cup.runtime.Symbol(CompilerSymbol.EOF); }
            }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
