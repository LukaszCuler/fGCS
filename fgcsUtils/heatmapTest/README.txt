AnBnGrammar.txt
-------------------------
Grammar in form:
$->AB (1.0)
$->AC (1.0)
C->$B (1.0)
A->DB (0.2)

A->a
D->a
B->b

A^nB^n language, with support of erorrs in form of possible 'a' symbol replacement by 'ab'

AsveldGrammar.txt
-------------------------
Grammar in form:
$->AB (1.0)
$->BA (1.0)
$->AA (0.1)
$->BB (0.4)
A->$A (1.0)
A->A$ (1.0)
B->B$ (1.0)
B->$B (1.0)

A->a
B->b

Language obtained from:
Fuzzy context-free languages—Part 1: Generalized fuzzy context-free grammars
https://www.sciencedirect.com/science/article/pii/S0304397505003592

This grammar describes language with equal number of 'a' and 'b' without precise order. Additional 'a' and 'b' are modelled, where additional 'a' are considered as more crucial than 'b'. Original membership value for $->BB was modified to 0.4.

Originally language was defined as:
• u(w;L1) = 1 if and only if #a(w) = #b(w) and w != lambda,
• u(w;L1) = 0.9 if and only if #b(w)#a(w) + 2 and |w| is even,
• u(w;L1) = 0.1 if and only if #a(w)#b(w) + 2 and |w| is even,
• u(w;L1) = 0 if and only if either w = lambda or |w| is odd

u() is membership and #() is number of given symbol in w
