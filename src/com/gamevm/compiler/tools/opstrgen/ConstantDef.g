grammar ConstantDef;

options {
  language = Java;
}

@header {
package com.gamevm.compiler.tools.opstrgen;

import java.lang.StringBuilder;
}

@lexer::header {
package com.gamevm.compiler.tools.opstrgen;
}

@members {
private StringBuilder b = new StringBuilder();
private int i = 0;
}

constants returns [String result]: { b.append("public static final String[] strings = new String[] {"); } constant* { b.append("}"); $result = b.toString(); };

constant: 
'public' 'static' 'final' 'int' IDENT '=' INTEGER_LITERAL ';' 
{ 
  if (i != 0)
    b.append(", ");
  b.append('"');
  b.append($IDENT.text);
  b.append('"'); 
  i++;
}
;

fragment CHAR: 'a'..'z' | 'A'..'Z';
fragment DIGIT: '0'..'9';
fragment NL : ('\n' | '\r');

INTEGER_LITERAL: DIGIT+;
IDENT: CHAR (CHAR | DIGIT | '_')* ;
WS: (' ' | '\t' | '\r' | '\n' | '\f')+ { $channel = HIDDEN; };

COMMENT: '//' (~('\n' | '\r'))* NL* { $channel = HIDDEN; };
MULTILINE_COMMENT: '/*' .* '*/' { $channel = HIDDEN; };
