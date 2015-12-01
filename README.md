# A compiler for a Java-like language
This is a compiler for a Java-like language, Aura, with classes, generics, return type overloading, type inference 
and implicit casting. The compiler was created as a class project 




/*
Déscription basique de langage: Notre langage de programmation, Aura, est un langage impératif, structuré, orienté objet de type Java. Il utilise le typage fort et statique mais le compilateur est capable d’inférence dans beaucoup de situations. Les pointeurs ne peuvent pas être utilisés directement mais les objets des types non-prédéfinis se trouvent en général sur le tas et on les accès par référence uniquement. Allocation dynamique se passe à chaque fois qu’on construit un nouveau objet ou un nouveau tableau. Le langage supporte la surcharge, même basée sur le type de retour, et la coercition de nombres. Le langage supporte aussi toutes les structures de controle expectées (foreach, if, for, while…) et l’analyse de flot. (voir la spécification du langage complète)

Exemple de code source Aura:
1: result : integer;
2: function factorial(n : integer) : integer {
3:   if (n == 0) return 1;
4:   return n * factorial(n - 1);
5: }
6: procedure main() {
7:   result = factorial(5);
8:   print_int(result);
9: }
Avec le commande “java compiler.Main source.txt” il est possible d’obtenir la réponse correcte 120. Notons que la récursion fonctionne correctement. La fonction print_int sur la ligne 8 n’est pas défini par utilisateur - il s’agit d’un “appel système” de MARS, l’emulateur MIPS qu’on utilise pour executer le code.

Déscription générale de compilateur: Le compilateur accepte un fichier source, utilise le code généré par Flex et CUP pour faire l’analyse lexicale et syntaxique et, en même temps, il effectue l’analyse sémantique qui crée l’arbre de syntaxe abstraite. Il utilise le paradigme de compilation dirigée par la syntaxe. 

L’analyse lexicale n’arrête jamais la compilation. Un erreur syntaxique aussi n’arrête pas la compilation, mais en cas de problèmes de syntaxe graves, le reste du code ne sera pas accepté par le CUP. Nous avons laissé les paramètres de géstion des erreurs de CUP sur les valeurs par défaut.  L’analyse sémantique n’arrête jamais la compilation.

À la fin de ce processus, seulement si aucun erreur n’est survenu, la génération du code intermédiare est lancée. Le code intermédiare est un code à trois addresses qui n’existe que dans la mémoire du compilateur (mais il est imprimé sur la sortie standarde). La génération du code intermédiaire n’échoue jamais.

Finalement, le code intermédiare est transformé en assembler MIPS sous forme de texte source. Ce code peut être interpreté par un interpreteur MIPS. Nous avons choisi le logiciel MARS qui fournit aussi quelques appels système de base. Cette transformation peut échouer - et ce, si le code Aura contient des opérations légales dans le langage mais dont la traduction en MIPS n’était pas implementée (voir Limitations principales du compilateur).
(voir le code source à GitHub)
*/
