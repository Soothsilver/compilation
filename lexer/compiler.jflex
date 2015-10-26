package compiler.generated;
import java_cup.runtime.*;
import compiler.Compilation;

%%
%class CompilerLexer
%line
%column
%cupsym CompilerSymbol
%cup
%public

%{
public Compilation compilation;
private String stringBeingBuilt;
private String characterBeingBuilt;

private Symbol symbol (int type) {
        return new Symbol (type, yyline, yycolumn);
}

private Symbol symbol (int type, Object value) {
        return new Symbol (type, yyline, yycolumn, value);
}
%}

identifier  = [a-zA-Z_][a-zA-Z0-9_]*
hexchar 	= 0x[0-9A-F]*
int 		= [0-9]*
float 		= [0-9]*\.[0-9]+(e-?[0-9]+)? | [0-9]+\.[0-9]*(e-?[0-9]+)?
			| [0-9]*[\.]?[0-9]+(e-?[0-9]+) | [0-9]+[\.]?[0-9]*(e-?[0-9]+)

%xstate STRING
%xstate LONG_COMMENT

%%

/* -------------------------------------------------
        Operators 
   ------------------------------------------------- */

"*"      { return symbol(CompilerSymbol.TIMES); }
"/"      { return symbol(CompilerSymbol.DIVIDE); }
"%"      { return symbol(CompilerSymbol.MODULO); }
"<"      { return symbol(CompilerSymbol.LESSTHAN); }
"<="     { return symbol(CompilerSymbol.LESSOREQUAL); }
">"      { return symbol(CompilerSymbol.GREATERTHAN); }
">="     { return symbol(CompilerSymbol.GREATEROREQUAL); }
"=="     { return symbol(CompilerSymbol.EQUALEQUAL); }
"!="     { return symbol(CompilerSymbol.UNEQUAL); }
"&&"     { return symbol(CompilerSymbol.LOGICALAND); }
"||"     { return symbol(CompilerSymbol.LOGICALOR); }
"&"      { return symbol(CompilerSymbol.BITWISEAND); }
"|"      { return symbol(CompilerSymbol.BITWISEOR); }
"^"      { return symbol(CompilerSymbol.XOR); }
"<<"     { return symbol(CompilerSymbol.SHIFTLEFT); }
">>"     { return symbol(CompilerSymbol.SHIFTRIGHT); }
"="      { return symbol(CompilerSymbol.EQUAL); }
"+="     { return symbol(CompilerSymbol.UNARYPLUS); }
"-="     { return symbol(CompilerSymbol.UNARYMINUS); }
"*="     { return symbol(CompilerSymbol.UNARYTIMES); }
"/="     { return symbol(CompilerSymbol.UNARYDIVIDE); }
"%="     { return symbol(CompilerSymbol.UNARYMODULO); }
"<<="    { return symbol(CompilerSymbol.UNARYSHIFTLEFT); }
">>="    { return symbol(CompilerSymbol.UNARYSHIFTRIGHT); }
"&="     { return symbol(CompilerSymbol.UNARYBITWISEAND); }
"^="     { return symbol(CompilerSymbol.UNARYXOR); }
"|="     { return symbol(CompilerSymbol.UNARYBITWISEOR); }
"++"     { return symbol(CompilerSymbol.INCREMENT); }
"--"     { return symbol(CompilerSymbol.DECREMENT); }
"!"      { return symbol(CompilerSymbol.LOGICALNEGATION); }
"~"      { return symbol(CompilerSymbol.BITWISENEGATION); }
"-"      { return symbol(CompilerSymbol.MINUS); }
"+"      { return symbol(CompilerSymbol.PLUS); }
"@"		 { return symbol(CompilerSymbol.CONCATENATE); }
"[["     { return symbol(CompilerSymbol.LEFTTYPEBRACKET); }
"]]"     { return symbol(CompilerSymbol.RIGHTTYPEBRACKET); }

/* -------------------------------------------------
        Punctuation
   ------------------------------------------------- */
   
"{"      { return symbol(CompilerSymbol.LBRACE); }
"}"      { return symbol(CompilerSymbol.RBRACE); }
"("      { return symbol(CompilerSymbol.LPAR); }
")"      { return symbol(CompilerSymbol.RPAR); }
"["      { return symbol(CompilerSymbol.LBRACKET); }
"]"      { return symbol(CompilerSymbol.RBRACKET); }

":"      { return symbol(CompilerSymbol.COLON); }
";"      { return symbol(CompilerSymbol.SEMICOLON); }
","      { return symbol(CompilerSymbol.COMMA); }
"."      { return symbol(CompilerSymbol.PERIOD); }

/* -------------------------------------------------
        Keywords 
   ------------------------------------------------- */
   
"procedure"   { return symbol(CompilerSymbol.PROCEDURE); }
"function"    { return symbol(CompilerSymbol.FUNCTION); }
"list"        { return symbol(CompilerSymbol.LIST); }
"of"          { return symbol(CompilerSymbol.OF); }
"class"       { return symbol(CompilerSymbol.CLASS); }
"structure"   { return symbol(CompilerSymbol.STRUCTURE); }
"type"        { return symbol(CompilerSymbol.TYPE); }
"return"      { return symbol(CompilerSymbol.RETURN); }
"stop"        { return symbol(CompilerSymbol.STOP); }
"break"       { return symbol(CompilerSymbol.BREAK); }
"if"          { return symbol(CompilerSymbol.IF); }
"else"        { return symbol(CompilerSymbol.ELSE); }
"foreach"     { return symbol(CompilerSymbol.FOREACH); }
"for"	      { return symbol(CompilerSymbol.FOR); }
"in"          { return symbol(CompilerSymbol.IN); }
"while"       { return symbol(CompilerSymbol.WHILE); }
"repeat"      { return symbol(CompilerSymbol.REPEAT); }
"new"         { return symbol(CompilerSymbol.NEW); }
"null"        { return symbol(CompilerSymbol.NULL); }
"true"        { return symbol(CompilerSymbol.BOOLEAN, true); }
"false"       { return symbol(CompilerSymbol.BOOLEAN, false); }
"debug_spawn_structure" { return symbol(CompilerSymbol.DEBUG_SPAWN_STRUCTURE); }

/* -------------------------------------------------
        Identifier (must be after keywords) 
   ------------------------------------------------- */
   
{identifier}     { return symbol(CompilerSymbol.IDENTIFIER, yytext()); }

/* -------------------------------------------------
        Comments 
   ------------------------------------------------- */
   
"//" [^\r\n]* (\r|\n|\r\n|\n|\r)? { /* ignore line comments */ }
"/*" { yybegin(LONG_COMMENT); }

<LONG_COMMENT> {
 "*/" { yybegin(YYINITIAL); }
 <<EOF>> {
   yybegin(YYINITIAL);
   compilation.lexicalError("End of file reached while parsing a multiline comment. You forgot to close it with '*/'.", yyline, yycolumn);
 }
 . { /* do nothing */ }
 "\n" { /* do nothing */ }
}

/* -------------------------------------------------
        Strings 
   ------------------------------------------------- */
\"	          { yybegin(STRING); stringBeingBuilt = new String(); }
<STRING> {
	 \\\\	{ stringBeingBuilt += "\\"; }
	 \\\"	{ stringBeingBuilt += "\""; }
	 \r 	{ stringBeingBuilt += "\r"; }	 
	 \n 	{ stringBeingBuilt += "\n"; }
	 \\n	{ stringBeingBuilt += "\n"; }
	 \\r 	{ stringBeingBuilt += "\r"; }	
	 \\. 	{ 
	 	 compilation.lexicalError("Unrecognized escape sequence '" + yytext() + "' within a string.", yyline, yycolumn); 
     }
	 \"		{ 
		 yybegin(YYINITIAL); 
		 return symbol(CompilerSymbol.STRING, stringBeingBuilt); 
	 }
	 <<EOF>> {
		 yybegin(YYINITIAL); 
	 	 compilation.lexicalError("End of file reached while parsing a string. You forgot to close the double quotes.", yyline, yycolumn);
	 	 return symbol(CompilerSymbol.STRING, stringBeingBuilt); 
	 }
	 .	{stringBeingBuilt += yytext(); }
}

/* -------------------------------------------------
        Constants
   ------------------------------------------------- */

{int}    {
    int number;
    try {
       number = Integer.parseInt(yytext());
    } catch (NumberFormatException ex) {
       number = 0;
       compilation.lexicalError("The integer '" + yytext() + "' is too large to fit in 32 bits.", yyline, yycolumn);
    }
    return symbol(CompilerSymbol.INTEGER,number );
    }
{float}  {
    float number = Float.parseFloat(yytext());
    if (Float.isInfinite(number)) {
        compilation.lexicalError("The number '" + yytext() + "' is too large to be represented as a floating-point number.", yyline, yycolumn);
    }
    if (number == 0) {
            compilation.lexicalError("The number '" + yytext() + "' is too small to be accurately represented as a floating-point number. Use '0' instead if that was the intention.", yyline, yycolumn);
    }
    return symbol(CompilerSymbol.FLOAT, number);
    }

/* -------------------------------------------------
        Characters
   ------------------------------------------------- */
\'\\\'\'	{ return symbol(CompilerSymbol.CHARACTER, '\''); }
\'\\\"\'	{ return symbol(CompilerSymbol.CHARACTER, '\"'); }
\'\\n\'		{ return symbol(CompilerSymbol.CHARACTER, '\n'); }
\'\\r\'		{ return symbol(CompilerSymbol.CHARACTER, '\r'); }
\'\\\\\'	{ return symbol(CompilerSymbol.CHARACTER, '\\'); }
\'.\'	    { return symbol(CompilerSymbol.CHARACTER,  yytext().toCharArray()[1]); }
\'	{ compilation.lexicalError("An apostrophe encountered that is not part of any valid character constant. Skipping.", yyline, yycolumn);  }

{hexchar} {
	int number;
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

 /* -------------------------------------------------
        Whitespace
   ------------------------------------------------- */

[\ |\t|\n|\r|\r\n|\n\r]      { /* ignore whitespace */ }

/* -------------------------------------------------
        Any other character triggers an error
   ------------------------------------------------- */
   
. { compilation.lexicalError("Unknown character '" + yytext() + "' encountered. Skipping.", yyline, yycolumn); }
